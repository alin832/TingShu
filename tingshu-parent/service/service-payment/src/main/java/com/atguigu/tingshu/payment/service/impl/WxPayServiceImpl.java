package com.atguigu.tingshu.payment.service.impl;

import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.model.payment.PaymentInfo;
import com.atguigu.tingshu.payment.config.WxPayV3Config;
import com.atguigu.tingshu.payment.service.PaymentInfoService;
import com.atguigu.tingshu.payment.service.WxPayService;
import com.atguigu.tingshu.payment.util.PayUtil;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.jsapi.JsapiServiceExtension;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.model.Transaction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WxPayServiceImpl implements WxPayService {

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Autowired
    private RSAAutoCertificateConfig rsaAutoCertificateConfig;

    @Autowired
    private WxPayV3Config wxPayV3Config;

    /**
     * 微信下单
     *
     * @param paymentType
     * @param orderNo
     * @return
     */
    @Override
    public Map<String, String> createJsapi(String paymentType, String orderNo) {
        //获取用户ID
        Long userId = AuthContextHolder.getUserId();

        //保存本次交易信息
        PaymentInfo paymentInfo = paymentInfoService.savePaymentInfo(paymentType, orderNo, userId);

        //对接微信支付接口
        // 构建service
        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(rsaAutoCertificateConfig).build();
        // request.setXxx(val)设置所需参数，具体参数可见Request定义
        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        //单位分为1
        amount.setTotal(1);
        request.setAmount(amount);
        request.setAppid(wxPayV3Config.getAppid());
        request.setMchid(wxPayV3Config.getMerchantId());
        request.setDescription(paymentInfo.getContent());
        //异步回调，在微信处理成功后，调用当前设置的异步地址，处理后续业务
        request.setNotifyUrl(wxPayV3Config.getNotifyUrl());

        request.setOutTradeNo(orderNo);

        //设置开发者信息
        Payer payer = new Payer();
        payer.setOpenid("odo3j4ujPBRopdATZnxKZ3HDOLAc");

        request.setPayer(payer);
        // 调用下单方法，得到应答
        PrepayWithRequestPaymentResponse paymentResponse = service.prepayWithRequestPayment(request);
        //创建对象封装数据
        Map<String, String> resultMap = new HashMap<>();

        // 获取对应信息
        if (paymentResponse != null) {

            String timeStamp = paymentResponse.getTimeStamp();
            String packageVal = paymentResponse.getPackageVal();
            String paySign = paymentResponse.getPaySign();
            String signType = paymentResponse.getSignType();
            String nonceStr = paymentResponse.getNonceStr();


            resultMap.put("timeStamp", timeStamp);
            resultMap.put("package", packageVal);
            resultMap.put("paySign", paySign);
            resultMap.put("signType", signType);
            resultMap.put("nonceStr", nonceStr);
        }

        return resultMap;
    }

    /**
     * 微信同步状态查询
     *
     * @param orderNo
     * @return
     */
    @Override
    public Boolean queryPayStatus(String orderNo) {


        //构建查询请求对象
        QueryOrderByOutTradeNoRequest queryRequest = new QueryOrderByOutTradeNoRequest();
        queryRequest.setMchid(wxPayV3Config.merchantId);
        queryRequest.setOutTradeNo(orderNo);

        // 构建service
        JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(rsaAutoCertificateConfig).build();
        //调用接口执行查询
        Transaction transaction = service.queryOrderByOutTradeNo(queryRequest);
        System.out.println(transaction.getTradeState());

        //判断
        if (transaction != null) {
            Transaction.TradeStateEnum tradeState = transaction.getTradeState();
            if (Transaction.TradeStateEnum.SUCCESS.equals(tradeState)) {

                return true;
            }
        }
        return false;
    }

    /**
     * 微信支付异步回调
     *
     * @param request
     * @return
     */
    @Override
    public Map<String, String> wxPayNotify(HttpServletRequest request) {
        //封装返回处理结果
        Map<String,String> resultMap=new HashMap<>();
        //获取请求头携带的参数
        String wechatPaySerial = request.getHeader("Wechatpay-Serial");  //签名
        String wechatpayNonce = request.getHeader("Wechatpay-Nonce");  //签名中的随机数
        String wechatTimestamp = request.getHeader("Wechatpay-Timestamp"); //时间戳
        String wechatSignature = request.getHeader("Wechatpay-Signature"); //签名类型

        //获取HTTP 请求体 body
        //切记使用原始报文，不要用 JSON 对象序列化后的字符串，避免验签的 body 和原文不一致。
        String requestBody = PayUtil.readData(request);

        // 构造 RequestParam
        RequestParam requestParam = new RequestParam.Builder()
                .serialNumber(wechatPaySerial)
                .nonce(wechatpayNonce)
                .signature(wechatSignature)
                .timestamp(wechatTimestamp)
                .body(requestBody)
                .build();


        // 初始化 NotificationParser
        NotificationParser parser = new NotificationParser(rsaAutoCertificateConfig);


        // 以支付通知回调为例，验签、解密并转换成 Transaction
        Transaction transaction = parser.parse(requestParam, Transaction.class);

        try {
            //判断
            if(transaction!=null){
                //判断支付状态
                if(Transaction.TradeStateEnum.SUCCESS.equals(transaction.getTradeState())){

                    //获取金额
                    Integer payerTotal = transaction.getAmount().getPayerTotal();
                    //判断
                    if(payerTotal.intValue()==1){

                        //更新支付记录
                        paymentInfoService.updatePaymentInfoSuccess(transaction);

                        resultMap.put("code","SUCCESS");
                        resultMap.put("message","处理成功");

                        return resultMap;
                    }


                }


            }
        } catch (Exception e) {
            resultMap.put("code","FAIL");
            resultMap.put("message","处理失败");
            log.error("处理微信支付异步回调异常。。。。");
        }




        return resultMap;
    }
}
