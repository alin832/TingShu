package com.atguigu.tingshu.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataUnit;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.atguigu.tingshu.account.AccountFeignClient;
import com.atguigu.tingshu.album.AlbumFeignClient;
import com.atguigu.tingshu.common.constant.KafkaConstant;
import com.atguigu.tingshu.common.constant.RedisConstant;
import com.atguigu.tingshu.common.constant.SystemConstant;
import com.atguigu.tingshu.common.delay.DelayMsgService;
import com.atguigu.tingshu.common.execption.GuiguException;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.model.album.AlbumInfo;
import com.atguigu.tingshu.model.album.TrackInfo;
import com.atguigu.tingshu.model.order.OrderDerate;
import com.atguigu.tingshu.model.order.OrderDetail;
import com.atguigu.tingshu.model.order.OrderInfo;
import com.atguigu.tingshu.model.user.VipServiceConfig;
import com.atguigu.tingshu.order.helper.SignHelper;
import com.atguigu.tingshu.order.mapper.OrderDerateMapper;
import com.atguigu.tingshu.order.mapper.OrderDetailMapper;
import com.atguigu.tingshu.order.mapper.OrderInfoMapper;
import com.atguigu.tingshu.order.service.OrderInfoService;
import com.atguigu.tingshu.user.client.UserFeignClient;
import com.atguigu.tingshu.vo.account.AccountLockVo;
import com.atguigu.tingshu.vo.order.OrderDerateVo;
import com.atguigu.tingshu.vo.order.OrderDetailVo;
import com.atguigu.tingshu.vo.order.OrderInfoVo;
import com.atguigu.tingshu.vo.order.TradeVo;
import com.atguigu.tingshu.vo.user.UserInfoVo;
import com.atguigu.tingshu.vo.user.UserPaidRecordVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressWarnings({"all"})
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;


    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private AlbumFeignClient albumFeignClient;

    /**
     * 订单确认
     *
     * @param tradeVo
     * @return
     */
    @Override
    public OrderInfoVo tradeData(TradeVo tradeVo) {

        //创建订单确认对象
        OrderInfoVo orderInfoVo = new OrderInfoVo();

        //获取
        Long userId = AuthContextHolder.getUserId();

        //获取用户选择的付费项目
        String itemType = tradeVo.getItemType();

        //设置付费类型
        orderInfoVo.setItemType(itemType);

        //定义封装的金额变量
        //订单原始金额
        BigDecimal originalAmount = new BigDecimal("0.00");
        //减免总金额
        BigDecimal derateAmount = new BigDecimal("0.00");
        //订单总金额
        BigDecimal orderAmount = new BigDecimal("0.00");


        //定义封装的详情集合
        //订单详情集合
        List<OrderDetailVo> orderDetailVoList = new ArrayList<>();
        //订单减免明细列表
        List<OrderDerateVo> orderDerateVoList = new ArrayList<>();

        //判断 1003
        if (SystemConstant.ORDER_ITEM_TYPE_VIP.equals(itemType)) {
            //vip确认
            //根据用户选择的套餐查询套餐详情
            Long itemId = tradeVo.getItemId();
            //查询数据
            VipServiceConfig vipServiceConfig = userFeignClient.getVipServiceConfig(itemId).getData();
            //判断
            Assert.notNull(vipServiceConfig, "查询vip套餐异常，套餐id:{}", itemId);
            //获取原价
            originalAmount = vipServiceConfig.getPrice();
            //获取优惠后价格
            orderAmount = vipServiceConfig.getDiscountPrice();
            //计算优惠价格
            derateAmount = originalAmount.subtract(orderAmount);
            //封装订单明细

            OrderDetailVo orderDetailVo = new OrderDetailVo();
            orderDetailVo.setItemId(itemId);
            orderDetailVo.setItemName(vipServiceConfig.getName());
            orderDetailVo.setItemUrl(vipServiceConfig.getImageUrl());
            orderDetailVo.setItemPrice(originalAmount);
            orderDetailVoList.add(orderDetailVo);


            //封装优惠明细
            OrderDerateVo orderDerateVo = new OrderDerateVo();
            orderDerateVo.setDerateType(SystemConstant.ORDER_DERATE_VIP_SERVICE_DISCOUNT);
            orderDerateVo.setDerateAmount(derateAmount);
            orderDerateVo.setRemarks("VIP限时优惠：" + derateAmount);

            orderDerateVoList.add(orderDerateVo);


        } else if (SystemConstant.ORDER_ITEM_TYPE_ALBUM.equals(itemType)) {
            //专辑确认
            //获取专辑ID
            Long albumId = tradeVo.getItemId();
            //根据专辑ID查询是否购买过本专辑
            Result<Boolean> paidAlbum = userFeignClient.isPaidAlbum(albumId);
            Boolean isBuy = paidAlbum.getData();
//            Boolean isBuy = userFeignClient.isPaidAlbum(albumId).getData();
            if (isBuy) {
                throw new GuiguException(400, "已经购买过该专辑，请。。。。。");
            }
            //获取专辑信息
            AlbumInfo albumInfo = albumFeignClient.getAlbumInfo(albumId).getData();
            Assert.notNull(albumInfo, "查询专辑异常，专辑ID:{}", albumId);

            //获取用户信息
            UserInfoVo userInfoVo = userFeignClient.getUserInfoVo(userId).getData();
            Assert.notNull(userInfoVo, "查询用户异常，用户ID:{}", userId);

            //获取金额
            originalAmount = albumInfo.getPrice();
            //订单价格
            orderAmount = originalAmount;


            //判断是否存在普通会员折扣
            if (albumInfo.getDiscount().intValue() != -1) {
                //有普通用户折扣
                if (userInfoVo.getIsVip() == 0) { //  100  8
                    orderAmount = originalAmount.multiply(albumInfo.getDiscount()).divide(new BigDecimal(10), 2, RoundingMode.HALF_UP);

                }

                if (userInfoVo.getIsVip() == 1 && new Date().after(userInfoVo.getVipExpireTime())) {

                    orderAmount = originalAmount.multiply(albumInfo.getDiscount()).divide(new BigDecimal(10), 2, RoundingMode.HALF_UP);

                }


            }


            //判断是否存在vip折扣
            if (albumInfo.getVipDiscount().intValue() != -1) {

                //判断是否为vip


                if (userInfoVo.getIsVip() == 1 && new Date().before(userInfoVo.getVipExpireTime())) {


                    orderAmount = originalAmount.multiply(albumInfo.getVipDiscount()).divide(new BigDecimal(10), 2, RoundingMode.HALF_UP);
                }


            }


            //计算优惠金额
            derateAmount = originalAmount.subtract(orderAmount);


            //封装集合
            //封装订单详情
            OrderDetailVo orderDetailVo = new OrderDetailVo();
            orderDetailVo.setItemId(albumId);
            orderDetailVo.setItemName(albumInfo.getAlbumTitle());
            orderDetailVo.setItemUrl(albumInfo.getCoverUrl());
            orderDetailVo.setItemPrice(originalAmount);
            orderDetailVoList.add(orderDetailVo);


            //封装优惠集合列表
            if (derateAmount.intValue() > 0) {

                OrderDerateVo orderDerate = new OrderDerateVo();
                orderDerate.setDerateType(SystemConstant.ORDER_DERATE_ALBUM_DISCOUNT);
                orderDerate.setDerateAmount(derateAmount);
                orderDerate.setRemarks("专辑优惠：" + derateAmount);

                orderDerateVoList.add(orderDerate);
            }


        } else if (SystemConstant.ORDER_ITEM_TYPE_TRACK.equals(itemType)) {
            //声音确认
            //查询用户待购买的声音列表
            List<TrackInfo> trackInfoList = albumFeignClient.findPaidTrackInfoList(tradeVo.getItemId(), tradeVo.getTrackCount()).getData();
            //判断
            if (CollectionUtils.isEmpty(trackInfoList)) {

                throw new GuiguException(400, "该专辑没有符合条件的声音列表");
            }
            //查询专辑信息
            AlbumInfo albumInfo = albumFeignClient.getAlbumInfo(trackInfoList.get(0).getAlbumId()).getData();

            //获取声音价格
            BigDecimal price = albumInfo.getPrice();

            //计算==每集价格*数量
            //赋值原金额
            originalAmount = price.multiply(new BigDecimal(tradeVo.getTrackCount()));
            //赋值订单金额
            orderAmount = originalAmount;

            //封装订单详情集合

            orderDetailVoList = trackInfoList.stream().map(trackInfo -> {

                OrderDetailVo orderDetailVo = new OrderDetailVo();
                orderDetailVo.setItemId(trackInfo.getId());
                orderDetailVo.setItemName(trackInfo.getTrackTitle());
                orderDetailVo.setItemUrl(trackInfo.getCoverUrl());
                orderDetailVo.setItemPrice(price);

                return orderDetailVo;
            }).collect(Collectors.toList());
        }

        //设置金额
        orderInfoVo.setOriginalAmount(originalAmount);
        orderInfoVo.setDerateAmount(derateAmount);
        orderInfoVo.setOrderAmount(orderAmount);
        //设置集合
        orderInfoVo.setOrderDetailVoList(orderDetailVoList);
        orderInfoVo.setOrderDerateVoList(orderDerateVoList);


        //统一处理参数

        //tradeNo交易号处理--防止订单重复提交的
        String tradeNo = IdUtil.fastUUID();
        //定义交易号存储key
        String tradeKey = RedisConstant.ORDER_TRADE_NO_PREFIX + userId;
        //存储数据redis
        redisTemplate.opsForValue().set(tradeKey, tradeNo, 5, TimeUnit.MINUTES);
        //设置交易号
        orderInfoVo.setTradeNo(tradeNo);


        //设置时间戳
        orderInfoVo.setTimestamp(DateUtil.current());
        //将对象转换成map集合
        Map<String, Object> beanToMap = BeanUtil.beanToMap(orderInfoVo, false, true);
        //生成签名--防止订单确认后的信息被篡改
        String sign = SignHelper.getSign(beanToMap);
        //设置签名
        orderInfoVo.setSign(sign);



        return orderInfoVo;
    }


    @Autowired
    private AccountFeignClient accountFeignClient;


    @Autowired
    private DelayMsgService delayMsgService;


    /**
     * 提交订单
     *
     * @param orderInfoVo
     * @param userId
     * @return
     */
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public Map<String, String> submitOrder(OrderInfoVo orderInfoVo, Long userId) {

        //防止订单重复提交

        //定义防重key
        String tradeKey = RedisConstant.ORDER_TRADE_NO_PREFIX + userId;
        //获取放重值 交易号
        String tradeNo = orderInfoVo.getTradeNo();
        //定义lua脚本
        String scritp = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
                "then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end";

        //定义lua脚本对象
        DefaultRedisScript<Boolean> defaultRedisScript = new DefaultRedisScript<Boolean>();
        defaultRedisScript.setScriptText(scritp);
        defaultRedisScript.setResultType(Boolean.class);

        //发送脚本到redis
        Boolean flag = (Boolean) redisTemplate.execute(defaultRedisScript, Arrays.asList(tradeKey), tradeNo);
        //判断
        if (!flag) {
            throw new GuiguException(400, "订单重复提交，请重新确认后提交。");
        }

        //防止订单信息被篡改

        //获取提交的确认订单信息
        Map<String, Object> beanToMap = BeanUtil.beanToMap(orderInfoVo);
        //移除payway--之前的加密是payway是空，被忽略的
        beanToMap.remove("payWay");

        SignHelper.checkSign(beanToMap);

        //保存订单
        OrderInfo orderInfo = this.saveOrder(orderInfoVo, userId);

        //获取支付方式
        String payWay = orderInfoVo.getPayWay();


        //判断如果为余额支付进行处理
        if(SystemConstant.ORDER_PAY_ACCOUNT.equals(payWay)){

            //封装信息对接用户账户，扣减金额
            AccountLockVo accountLockVo=new AccountLockVo();
            accountLockVo.setOrderNo(orderInfo.getOrderNo());
            accountLockVo.setAmount(orderInfoVo.getOrderAmount());
            accountLockVo.setUserId(userId);
            accountLockVo.setContent(orderInfo.getOrderTitle());


            //校验和扣减账户余额
            Result result=accountFeignClient.checkAndDeduct(accountLockVo);
            //判断
            if(200!=result.getCode()){
                throw new GuiguException(400,"账户扣减异常");
            }
            //扣减成功，虚拟发货
            UserPaidRecordVo userPaidRecordVo=new UserPaidRecordVo();
            userPaidRecordVo.setOrderNo(orderInfo.getOrderNo());
            userPaidRecordVo.setUserId(userId);
            userPaidRecordVo.setItemType(orderInfoVo.getItemType());

            List<Long> itemIdList = orderInfoVo.getOrderDetailVoList().stream().map(orderDetailVo -> orderDetailVo.getItemId()).collect(Collectors.toList());
            userPaidRecordVo.setItemIdList(itemIdList);

            //虚拟发货--新增用户购买记录
            Result paidRecordResult=userFeignClient.savePaidRecord(userPaidRecordVo);

            //判断
            if(200!=paidRecordResult.getCode()){

                throw new GuiguException(400,"新增购买记录异常。。");

            }



            //修改定订单状态
            orderInfo.setOrderStatus(SystemConstant.ORDER_STATUS_PAID);
            orderInfo.setUpdateTime(new Date());

            orderInfoMapper.updateById(orderInfo);
        }





        //编辑数据返回订单号
        Map<String,String> resultMap=new HashMap<>();
        resultMap.put("orderNo",orderInfo.getOrderNo());

        //发送延迟关单消息

        delayMsgService.sendDelayMessage(
                KafkaConstant.QUEUE_ORDER_CANCEL,
                orderInfo.getOrderNo(),
                Long.valueOf(KafkaConstant.DELAY_TIME)

        );


        return resultMap;
    }

    /**
     *  根据订单号获取订单相关信息
     * @param orderNo
     * @param userId
     * @return
     */
    @Override
    public OrderInfo getOrderInfo(String orderNo, Long userId) {

        //构建条件对象
        //select*from order_info where order_n=? and user_id=?
        QueryWrapper<OrderInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_no",orderNo);
        queryWrapper.eq("user_id",userId);

        //执行查询
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
        Long orderId = orderInfo.getId();
        //判断
        if(orderInfo!=null){

            //查询订单详情
            QueryWrapper<OrderDetail> orderDetailQueryWrapper = new QueryWrapper<>();
            orderDetailQueryWrapper.eq("order_id",orderId);


            List<OrderDetail> orderDetails = orderDetailMapper.selectList(orderDetailQueryWrapper);
            orderInfo.setOrderDetailList(orderDetails);

            //查询订单优惠
            QueryWrapper<OrderDerate> orderDerateQueryWrapper = new QueryWrapper<>();
            orderDerateQueryWrapper.eq("order_id",orderId);
            List<OrderDerate> orderDerates = orderDerateMapper.selectList(orderDerateQueryWrapper);
            orderInfo.setOrderDerateList(orderDerates);


        }


        //处理订单状态
        String orderStatusName=this.getOrderStatusName(orderInfo.getOrderStatus());

        //支付方式
        String payWayName=this.getPayWayName(orderInfo.getPayWay());


        orderInfo.setOrderStatusName(orderStatusName);
        orderInfo.setPayWayName(payWayName);


        return orderInfo;
    }

    /**
     * 分页查询我的订单列表
     * @param orderInfoPage
     * @param userId
     * @return
     */
    @Override
    public Page<OrderInfo> findUserPage(Page<OrderInfo> orderInfoPage, Long userId) {

        //分页查询列表
        orderInfoPage=  orderInfoMapper.selectUserPage(orderInfoPage,userId);
        //处理订单状态
        orderInfoPage.getRecords().forEach(orderInfo -> {

            orderInfo.setOrderStatusName(this.getOrderStatusName(orderInfo.getOrderStatus()));

        });


        return orderInfoPage;
    }

    /**
     * 延迟关单
     * @param orderNo
     */
    @Override
    public void orderCancal(String orderNo) {

        //根据订单号查询订单详情
        QueryWrapper<OrderInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_no",orderNo);
        //执行查询
        OrderInfo orderInfo = orderInfoMapper.selectOne(queryWrapper);
        //判断
        if(orderInfo!=null && SystemConstant.ORDER_STATUS_UNPAID.equals(orderInfo.getOrderStatus())){

            orderInfo.setOrderStatus(SystemConstant.ORDER_STATUS_CANCEL);
            orderInfo.setUpdateTime(new Date());
            //更新订单
            orderInfoMapper.updateById(orderInfo);

        }


    }

    /**
     * 支付成功更新订单
     * @param orderNo
     */
    @Override
    public void orderPaySuccess(String orderNo) {

        //查询订单
        OrderInfo orderInfo = orderInfoMapper.selectOne(new QueryWrapper<OrderInfo>().eq("order_no", orderNo));
        //判断
        if(!SystemConstant.ORDER_STATUS_UNPAID.equals(orderInfo.getOrderStatus())){
            return;
        }

        //判断支付状态
        orderInfo.setOrderStatus(SystemConstant.ORDER_STATUS_PAID);
        orderInfo.setUpdateTime(new Date());

        //修改订单信息
        orderInfoMapper.updateById(orderInfo);

        //虚拟发货
        //扣减成功，虚拟发货
        UserPaidRecordVo userPaidRecordVo=new UserPaidRecordVo();
        userPaidRecordVo.setOrderNo(orderInfo.getOrderNo());
        userPaidRecordVo.setUserId(orderInfo.getUserId());
        userPaidRecordVo.setItemType(orderInfo.getItemType());

        orderInfo = this.getOrderInfo(orderInfo.getOrderNo(), orderInfo.getUserId());
        List<Long> itemIdList = orderInfo.getOrderDetailList().stream().map(orderDetail -> orderDetail.getItemId()).collect(Collectors.toList());
        userPaidRecordVo.setItemIdList(itemIdList);

        //虚拟发货--新增用户购买记录
        Result paidRecordResult=userFeignClient.savePaidRecord(userPaidRecordVo);

        //判断
        if(200!=paidRecordResult.getCode()){

            throw new GuiguException(400,"新增购买记录异常。。");

        }




    }

    /**
     * 支付方式名称转换
     * @param payWay
     * @return
     */
    private String getPayWayName(String payWay) {

        if(SystemConstant.ORDER_PAY_WAY_WEIXIN.equals(payWay)){

            return "微信支付";
        }else if(SystemConstant.ORDER_PAY_WAY_ALIPAY.equals(payWay)){

            return "支付宝支付";
        }else if (SystemConstant.ORDER_PAY_ACCOUNT.equals(payWay)){

            return "余额支付";
        }else{
            return "支付方式不支持";
        }

    }

    /**
     * 处理订单状态名称转换
     * @param orderStatus
     * @return
     */
    private String getOrderStatusName(String orderStatus) {


        if(SystemConstant.ORDER_STATUS_UNPAID.equals(orderStatus)){


            return "未支付";
        }else if(SystemConstant.ORDER_STATUS_PAID.equals(orderStatus)){

            return "已支付";
        }else if (SystemConstant.ORDER_STATUS_CANCEL.equals(orderStatus)){

            return "已取消";
        }else{
            return "状态异常";
        }
    }





    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderDerateMapper orderDerateMapper;

    /**
     * 保存订单
     *
     * @param orderInfoVo
     * @param userId
     * @return
     */
    private OrderInfo saveOrder(OrderInfoVo orderInfoVo, Long userId) {

        //复制数据
        OrderInfo orderInfo = BeanUtil.copyProperties(orderInfoVo, OrderInfo.class);
        //设置用户ID
        orderInfo.setUserId(userId);
        //设置订单标题
        String itemName = orderInfoVo.getOrderDetailVoList().get(0).getItemName();
        orderInfo.setOrderTitle(itemName);
        //生成订单号
        String orderNo = DateUtil.today().replaceAll("-", "") + IdUtil.getSnowflakeNextId();
        orderInfo.setOrderNo(orderNo);
        //设置订单状态
        orderInfo.setOrderStatus(SystemConstant.ORDER_STATUS_UNPAID);
        //保存订单表
        orderInfoMapper.insert(orderInfo);

        //保存详情表
        List<OrderDetailVo> orderDetailVoList = orderInfoVo.getOrderDetailVoList();
        //判断
        if (CollectionUtils.isNotEmpty(orderDetailVoList)) {
            for (OrderDetailVo orderDetailVo : orderDetailVoList) {

                //复制数据
                OrderDetail orderDetail = BeanUtil.copyProperties(orderDetailVo, OrderDetail.class);
                orderDetail.setOrderId(orderInfo.getId());
                orderDetailMapper.insert(orderDetail);
            }


        }


        //保存减免表
        List<OrderDerateVo> orderDerateVoList = orderInfoVo.getOrderDerateVoList();
        //判断
        if (CollectionUtils.isNotEmpty(orderDerateVoList)) {
            for (OrderDerateVo orderDerateVo : orderDerateVoList) {

                OrderDerate orderDerate = BeanUtil.copyProperties(orderDerateVo, OrderDerate.class);
                orderDerate.setOrderId(orderInfo.getId());
                orderDerateMapper.insert(orderDerate);

            }
        }


        //返回订单号
        return orderInfo;
    }
}
