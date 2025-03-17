package com.atguigu.tingshu.order.service;

import com.atguigu.tingshu.model.order.OrderInfo;
import com.atguigu.tingshu.vo.order.OrderInfoVo;
import com.atguigu.tingshu.vo.order.TradeVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface OrderInfoService extends IService<OrderInfo> {


    /**
     * 订单确认
     * @param tradeVo
     * @return
     */
    OrderInfoVo tradeData(TradeVo tradeVo);

    /**
     * 提交订单
     * @param orderInfoVo
     * @param userId
     * @return
     */
    Map<String, String> submitOrder(OrderInfoVo orderInfoVo, Long userId);

    /**
     *  根据订单号获取订单相关信息
     * @param orderNo
     * @param userId
     * @return
     */
    OrderInfo getOrderInfo(String orderNo, Long userId);

    /**
     * 分页查询我的订单列表
     * @param orderInfoPage
     * @param userId
     * @return
     */
    Page<OrderInfo> findUserPage(Page<OrderInfo> orderInfoPage, Long userId);

    /**
     * 延迟关单
     * @param orderNo
     */
    void orderCancal(String orderNo);

    /**
     * 支付成功更新订单
     * @param orderNo
     */
    void orderPaySuccess(String orderNo);
}
