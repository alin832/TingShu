package com.atguigu.tingshu.payment.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.atguigu.tingshu.account.AccountFeignClient;
import com.atguigu.tingshu.common.constant.SystemConstant;
import com.atguigu.tingshu.common.execption.GuiguException;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.account.RechargeInfo;
import com.atguigu.tingshu.model.order.OrderInfo;
import com.atguigu.tingshu.model.payment.PaymentInfo;
import com.atguigu.tingshu.order.client.OrderFeignClient;
import com.atguigu.tingshu.payment.mapper.PaymentInfoMapper;
import com.atguigu.tingshu.payment.service.PaymentInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wechat.pay.java.service.payments.model.Transaction;
import io.netty.util.internal.UnstableApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@SuppressWarnings({"all"})
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {


    @Autowired
    private PaymentInfoMapper paymentInfoMapper;
    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private AccountFeignClient accountFeignClient;

    /**
     * 保存本次交易信息
     * @param paymentType
     * @param orderNo
     * @param userId
     * @return
     */
    @Override
    public PaymentInfo savePaymentInfo(String paymentType, String orderNo, Long userId) {

        //查询是否已经保存了记录
        QueryWrapper<PaymentInfo> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_no",orderNo);
        //执行查询
        Long count = paymentInfoMapper.selectCount(queryWrapper);
        //判断
        if(count>0){
           throw new GuiguException(400,"交易记录已存在");
        }

        //整合记录信息
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setUserId(userId);
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOrderNo(orderNo);
        paymentInfo.setPayWay(SystemConstant.ORDER_PAY_WAY_WEIXIN);
        paymentInfo.setOutTradeNo(orderNo);
        //支付类型 1301-订单 1302-充值
        if(SystemConstant.PAYMENT_TYPE_ORDER.equals(paymentType)){
            //根据订单号查询，订单信息1301-订单
            OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderNo).getData();
            //判断
            Assert.notNull(orderInfo,"查询订单信息异常，订单号：{}",orderNo);
            paymentInfo.setAmount(orderInfo.getOrderAmount());
            paymentInfo.setContent(orderInfo.getOrderTitle());

        }else if(SystemConstant.PAYMENT_TYPE_RECHARGE.equals(paymentType)){

            //调用账户微服务获取充值记录
            RechargeInfo rechargeInfo = accountFeignClient.getRechargeInfo(orderNo).getData();
            //判断
            Assert.notNull(rechargeInfo,"查询充值记录信息异常，订单号：{}",orderNo);

            paymentInfo.setAmount(rechargeInfo.getRechargeAmount());
            paymentInfo.setContent("充值金额："+rechargeInfo.getRechargeAmount());
        }
        //未支付
        paymentInfo.setPaymentStatus(SystemConstant.PAYMENT_STATUS_UNPAID);



        //保存记录 支付类型 1301-订单 1302-充值
        paymentInfoMapper.insert(paymentInfo);
        return paymentInfo;
    }

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 更新支付记录
     * @param transaction
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePaymentInfoSuccess(Transaction transaction) {


        //获取异步通知的唯一消息
        String transactionId = transaction.getTransactionId();
        //存储redis-setnx
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(transactionId, transactionId, 1445, TimeUnit.MINUTES);
        //判断
        if(!flag){
            return;
        }

        try {
            //获取支付记录
            String outTradeNo = transaction.getOutTradeNo();
            //构建查询条件
            QueryWrapper<PaymentInfo> queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("out_trade_no",outTradeNo);
            PaymentInfo paymentInfo = paymentInfoMapper.selectOne(queryWrapper);
            //判断
            if(paymentInfo==null){
                return;
            }
            if(!SystemConstant.PAYMENT_STATUS_UNPAID.equals(paymentInfo.getPaymentStatus())){

                return;
            }

            //更新支付记录信息

            paymentInfo.setPaymentStatus(SystemConstant.PAYMENT_STATUS_PAID);
            paymentInfo.setCallbackTime(new DateTime(transaction.getSuccessTime()));
            paymentInfo.setCallbackContent(JSON.toJSONString(transaction));
            paymentInfo.setUpdateTime(new Date());

            //更新支付记录
            paymentInfoMapper.updateById(paymentInfo);


            //判断处理本次支付是订单还是充值
            //订单处理
            if(SystemConstant.PAYMENT_TYPE_ORDER.equals(paymentInfo.getPaymentType())){

               Result result= orderFeignClient.orderPaySuccess(outTradeNo);

               //判断
                if(200!=result.getCode()){

                    throw new GuiguException(500, "远程修改订单状态异常：" + outTradeNo);
                }

            }

//
//            //处理充值
            if(SystemConstant.PAYMENT_TYPE_RECHARGE.equals(paymentInfo.getPaymentType())){

                Result result= accountFeignClient.rechargePaySuccess(outTradeNo);

                //判断
                if(200!=result.getCode()){

                    throw new GuiguException(500, "远程修改充值状态异常：" + outTradeNo);
                }

            }







        } catch (Exception e) {
            //避免在执行过程中出现异常，导致重试的微信回调无法进入
            redisTemplate.delete(transactionId);
        }


    }
}
