package com.atguigu.tingshu.user.startagy.impl;

import cn.hutool.core.date.DateUtil;
import com.atguigu.tingshu.model.user.UserInfo;
import com.atguigu.tingshu.model.user.UserVipService;
import com.atguigu.tingshu.model.user.VipServiceConfig;
import com.atguigu.tingshu.user.mapper.UserInfoMapper;
import com.atguigu.tingshu.user.mapper.UserVipServiceMapper;
import com.atguigu.tingshu.user.mapper.VipServiceConfigMapper;
import com.atguigu.tingshu.user.startagy.ItemTypeStrategy;
import com.atguigu.tingshu.vo.user.UserPaidRecordVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.netty.util.internal.UnstableApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @date: 2024/6/26 15:51
 * @author: yz
 * @version: 1.0
 */
@Component("1003")
public class VipStrategy implements ItemTypeStrategy {


    @Autowired
    private UserVipServiceMapper userVipServiceMapper;

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private VipServiceConfigMapper vipServiceConfigMapper;

    @Override
    public void savePaidRecord(UserPaidRecordVo userPaidRecordVo) {

        System.err.println("vip处理执行。。。。。");
        //新增vip记录和处理vip业务
        QueryWrapper<UserVipService> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_no",userPaidRecordVo.getOrderNo());
        //执行查询
        Long count = userVipServiceMapper.selectCount(queryWrapper);
        //判断
        if(count>0){
            return;
        }
        //添加vip服务购买记录
        UserVipService userVipService = new UserVipService();
        userVipService.setOrderNo(userPaidRecordVo.getOrderNo());
        userVipService.setUserId(userPaidRecordVo.getUserId());

        //根据用户ID查询用户信息
        UserInfo userInfoVo = userInfoMapper.selectById(userPaidRecordVo.getUserId());
        //获取vip状态字段
        Integer isVip = userInfoVo.getIsVip();
        //获取vip过期时间
        Date vipExpireTime = userInfoVo.getVipExpireTime();

        //根据本地购买套餐ID，查询套餐详情
        VipServiceConfig serviceConfig = vipServiceConfigMapper.selectById(userPaidRecordVo.getItemIdList().get(0));
        //获取套餐的服务月数
        Integer month = serviceConfig.getServiceMonth();

        //判断
        if(isVip.intValue()==1&&vipExpireTime.after(new Date())){
            //vip 本次购买开始时间为当前vip的过期时间
            userVipService.setStartTime(vipExpireTime);
            //设置本次购买vip的过期时间=上次过期时间+套餐的时间
            userVipService.setExpireTime(DateUtil.offsetMonth(vipExpireTime, month));

        }else{
            //普通用户或者过期的vip
            //开始时间
            userVipService.setStartTime(new Date());
            //结束实现
            userVipService.setExpireTime(DateUtil.offsetMonth(new Date(), month));
        }


        userVipServiceMapper.insert(userVipService);

        //更新user_info

        userInfoVo.setIsVip(1);
        userInfoVo.setVipExpireTime(userVipService.getExpireTime());
        userInfoVo.setUpdateTime(new Date());
        userInfoMapper.updateById(userInfoVo);


    }
}
