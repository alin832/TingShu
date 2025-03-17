package com.atguigu.tingshu.payment.service;

import com.atguigu.tingshu.model.payment.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wechat.pay.java.service.payments.model.Transaction;

public interface PaymentInfoService extends IService<PaymentInfo> {

    /**
     * 保存本次交易信息
     * @param paymentType
     * @param orderNo
     * @param userId
     * @return
     */
    PaymentInfo savePaymentInfo(String paymentType, String orderNo, Long userId);

    /**
     * 更新支付记录
     * @param transaction
     */
    void updatePaymentInfoSuccess(Transaction transaction);

}
