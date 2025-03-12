package com.atguigu.tingshu.account.mapper;

import com.atguigu.tingshu.model.account.UserAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {

    /**
     * 扣减用户账户余额
     * @param amount
     * @param userId
     * @return
     */
    @Update("update user_account set total_amount=total_amount-#{amount},\n" +
            "                        available_amount=available_amount-#{amount},\n" +
            "                        total_pay_amount=total_pay_amount+#{amount},\n" +
            "                        update_time=now()\n" +
            "              where user_id=#{userId} and is_deleted=0")
    int checkAndDeduct(@Param("amount") BigDecimal amount, @Param("userId") Long userId);

    /**
     * 更新账户金额
     * @param userId
     * @param amount
     * @return
     */
    @Update(" update user_account set total_amount=total_amount+#{amount},\n" +
            "                        available_amount=available_amount+#{amount},\n" +
            "                        total_income_amount= total_income_amount+#{amount},\n" +
            "                        update_time=now()\n" +
            "       where user_id=#{userId} and is_deleted=0 ")
    int updateUserAccoutnAmount(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

}
