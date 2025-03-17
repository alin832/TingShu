package com.atguigu.tingshu.account.service;

import com.atguigu.tingshu.model.account.UserAccount;
import com.atguigu.tingshu.model.account.UserAccountDetail;
import com.atguigu.tingshu.vo.account.AccountLockVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;

public interface UserAccountService extends IService<UserAccount> {


    /**
     * 初始化用户账户
     * @param userId
     */
    void saveUserAccount(Long userId);

    /**
     * 更新账户记录
     * @param userId
     * @param title
     * @param tradeType
     * @param amount
     * @param order_no
     */
     void saveUserAccountDetail(Long userId, String title, String tradeType, BigDecimal amount, String order_no);

    /**
     * 获取账户可用余额
     * @param userId
     * @return
     */
    BigDecimal getAvailableAmount(Long userId);


    /**
     * 扣减账户金额
     * @param accountLockVo
     */
    void checkAndLock(AccountLockVo accountLockVo);

    /**
     * 查询用户账户记录明细
     * @param userAccountDetailPage
     * @param userId
     * @param accountTradeTypeDeposit
     * @return
     */
    Page<UserAccountDetail> getUserAccountDetailPage(Page<UserAccountDetail> userAccountDetailPage, Long userId, String accountTradeTypeDeposit);
}
