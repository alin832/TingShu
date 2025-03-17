package com.atguigu.tingshu.account.impl;


import com.atguigu.tingshu.account.AccountFeignClient;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.account.RechargeInfo;
import com.atguigu.tingshu.vo.account.AccountLockVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AccountDegradeFeignClient implements AccountFeignClient {

    /**
     * 支付成功后充值处理
     * @param orderNo
     * @return
     */
    @Override
    public Result rechargePaySuccess(String orderNo) {
        log.error("调用账户微服务的rechargePaySuccess方法异常");
        return Result.fail();
    }

    /**
     * 扣减账户金额
     * @param accountLockVo
     * @return
     */
    @Override
    public Result checkAndDeduct(AccountLockVo accountLockVo) {
        log.error("调用账户微服务的checkAndDeduct方法异常");
        return Result.fail();
    }

    /**
     * 根据订单号获取充值信息
     * @param orderNo
     * @return
     */
    @Override
    public Result<RechargeInfo> getRechargeInfo(String orderNo) {
        log.error("调用账户微服务的getRechargeInfo方法异常");
        return Result.fail();
    }
}
