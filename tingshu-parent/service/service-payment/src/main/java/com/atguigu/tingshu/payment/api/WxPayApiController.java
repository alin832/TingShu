package com.atguigu.tingshu.payment.api;

import com.atguigu.tingshu.common.login.GuiguLogin;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.payment.service.WxPayService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "微信支付接口")
@RestController
@RequestMapping("api/payment")
@Slf4j
public class WxPayApiController {

    @Autowired
    private WxPayService wxPayService;




    /**
     * http://2iq27q.natappfree.cc/api/payment/wxPay/notify
     * 微信支付异步回调
     * @param request
     * @return
     *返回结果：
     * {
     *     "code": "FAIL",
     *     "message": "失败"
     * }
     *
     *
     */
    @PostMapping("/wxPay/notify")
    public Map<String,String>  wxPayNotify(HttpServletRequest request){



        Map<String,String> resultMap=wxPayService.wxPayNotify(request);


        return resultMap;
    }



    /**
     * 微信同步状态查询
     * api/payment/wxPay/queryPayStatus/{orderNo}
     * @param orderNo
     * @return
     */
    @GetMapping("/wxPay/queryPayStatus/{orderNo}")
    public Result<Boolean> queryPayStatus(@PathVariable String orderNo){


        Boolean flag=  wxPayService.queryPayStatus(orderNo);


        return Result.ok(flag);
    }



    /**
     * 微信下单
     * api/payment/wxPay/createJsapi/{paymentType}/{orderNo}
     * @param paymentType 支付类型 1301-订单 1302-充值
     * @param orderNo 订单号
     * @return
     */
    @PostMapping("/wxPay/createJsapi/{paymentType}/{orderNo}")
    @GuiguLogin
    public Result<Map<String,String>> createJsapi(@PathVariable String paymentType,
                                                  @PathVariable String orderNo){


        Map<String,String> resultMap=wxPayService.createJsapi(paymentType,orderNo);


        return Result.ok(resultMap);
    }


}
