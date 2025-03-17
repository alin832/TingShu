package com.atguigu.tingshu.account;

import com.atguigu.tingshu.account.impl.AccountDegradeFeignClient;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.account.RechargeInfo;
import com.atguigu.tingshu.vo.account.AccountLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 账号模块远程调用API接口
 * </p>
 *
 * @author atguigu
 */
@FeignClient(value = "service-account",path = "api/account", fallback = AccountDegradeFeignClient.class)
public interface AccountFeignClient {


    /**
     * api/account/rechargeInfo/rechargePaySuccess/{orderNo}
     * 支付成功后充值处理
     * @param orderNo
     * @return
     */
    @GetMapping("/rechargeInfo/rechargePaySuccess/{orderNo}")
    public Result rechargePaySuccess(@PathVariable String orderNo);

    /**
     * 扣减账户金额
     * api/account/userAccount/checkAndLock
     * @param accountLockVo
     * @return
     */
    @PostMapping("/userAccount/checkAndLock")
    public Result checkAndDeduct(@RequestBody AccountLockVo accountLockVo);

    /**
     * 根据订单号获取充值信息
     * api/account/rechargeInfo/getRechargeInfo/{orderNo}
     * @param orderNo
     * @return
     */
    @GetMapping("/rechargeInfo/getRechargeInfo/{orderNo}")
    public Result<RechargeInfo> getRechargeInfo(@PathVariable String orderNo);
}
