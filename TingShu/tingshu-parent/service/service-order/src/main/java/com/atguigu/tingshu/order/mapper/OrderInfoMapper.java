package com.atguigu.tingshu.order.mapper;

import com.atguigu.tingshu.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * 分页查询我的订单列表
     * @param orderInfoPage
     * @param userId
     * @return
     */
    Page<OrderInfo> selectUserPage(Page<OrderInfo> orderInfoPage, @Param("userId") Long userId);


    /**
     *  分页查询我的订单列表
     * @param pageInfo
     * @param userId
     * @return
     */
    Page<OrderInfo> getUserOrderByPage1(Page<OrderInfo> pageInfo, @Param("userId") Long userId);
}
