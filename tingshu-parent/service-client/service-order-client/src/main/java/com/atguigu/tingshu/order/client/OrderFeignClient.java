package com.atguigu.tingshu.order.client;

import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.order.OrderInfo;
import com.atguigu.tingshu.order.client.impl.OrderDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>
 * 订单模块远程调用API接口
 * </p>
 *
 * @author atguigu
 */
@FeignClient(value = "service-order",  path = "api/order",fallback = OrderDegradeFeignClient.class)
public interface OrderFeignClient {


    /**
     * api/order/orderInfo/orderPaySuccess/{orderNo}
     * 支付成功更新订单
     * @param orderNo
     * @return
     */
    @GetMapping("/orderInfo/orderPaySuccess/{orderNo}")
    public Result orderPaySuccess(@PathVariable String orderNo);

    /**
     * 根据订单号获取订单相关信息
     * api/order/orderInfo/getOrderInfo/{orderNo}
     *
     * @param orderNo
     * @return
     */
    @GetMapping("/orderInfo/getOrderInfo/{orderNo}")
    public Result<OrderInfo> getOrderInfo(@PathVariable String orderNo);


}
