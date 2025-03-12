package com.atguigu.tingshu.order.client.impl;


import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.order.OrderInfo;
import com.atguigu.tingshu.order.client.OrderFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderDegradeFeignClient implements OrderFeignClient {

    /**
     * 支付成功更新订单
     * @param orderNo
     * @return
     */
    @Override
    public Result orderPaySuccess(String orderNo) {

        log.error("【订单微服务】，调用方法gorderPaySuccess异常");
        return Result.fail();
    }

    /**
     *  根据订单号获取订单相关信息
     * @param orderNo
     * @return
     */
    @Override
    public Result<OrderInfo> getOrderInfo(String orderNo) {

        log.error("【订单微服务】，调用方法getOrderInfo异常");
        return Result.fail();
    }
}
