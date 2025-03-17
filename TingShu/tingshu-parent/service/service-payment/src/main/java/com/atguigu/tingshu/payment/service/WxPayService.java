package com.atguigu.tingshu.payment.service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface WxPayService {

    /**
     * 微信下单
     * @param paymentType
     * @param orderNo
     * @return
     */
    Map<String, String> createJsapi(String paymentType, String orderNo);

    /**
     * 微信同步状态查询
     * @param orderNo
     * @return
     */
    Boolean queryPayStatus(String orderNo);

    /**
     * 微信支付异步回调
     * @param request
     * @return
     */
    Map<String, String> wxPayNotify(HttpServletRequest request);
}
