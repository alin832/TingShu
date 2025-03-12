package com.atguigu.tingshu.account.service.impl;

import com.atguigu.tingshu.account.mapper.UserAccountDetailMapper;
import com.atguigu.tingshu.account.mapper.UserAccountMapper;
import com.atguigu.tingshu.account.service.UserAccountService;
import com.atguigu.tingshu.common.constant.SystemConstant;
import com.atguigu.tingshu.common.execption.GuiguException;
import com.atguigu.tingshu.model.account.UserAccount;
import com.atguigu.tingshu.model.account.UserAccountDetail;
import com.atguigu.tingshu.vo.account.AccountLockVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;

@Slf4j
@Service
@SuppressWarnings({"all"})
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

	@Autowired
	private UserAccountMapper userAccountMapper;

	@Autowired
	private UserAccountDetailMapper userAccountDetailMapper;

	/**
	 * 初始化用户账户
	 * @param userId
	 */
	@Override
	public void saveUserAccount(Long userId) {

		//创建账户对象
		UserAccount userAccount=new UserAccount();
		userAccount.setUserId(userId);
		//初始化账户，赠送部分金额
		userAccount.setTotalAmount(new BigDecimal(100));
		userAccount.setAvailableAmount(new BigDecimal(100));
		userAccount.setTotalIncomeAmount(new BigDecimal(100));

		userAccountMapper.insert(userAccount);

		//更新账户记录
		this.saveUserAccountDetail(userId,"初始化账户:赠送100", SystemConstant.ACCOUNT_TRADE_TYPE_DEPOSIT,new BigDecimal(100),null);
	}

	/**
	 *  记录账户变动明细
	 * @param userId
	 * @param title
	 * @param tradeType
	 * @param amount
	 * @param order_no
	 */
	@Override
	public void saveUserAccountDetail(Long userId, String title, String tradeType, BigDecimal amount, String order_no) {

		//创建用户账户明细对象
		UserAccountDetail userAccountDetail=new UserAccountDetail();
		userAccountDetail.setUserId(userId);
		userAccountDetail.setTitle(title);
		userAccountDetail.setTradeType(tradeType);
		userAccountDetail.setAmount(amount);
		userAccountDetail.setOrderNo(order_no);
		userAccountDetailMapper.insert(userAccountDetail);

	}

	/**
	 * 获取账户可用余额
	 * @param userId
	 * @return
	 */
	@Override
	public BigDecimal getAvailableAmount(Long userId) {

		//select *from user_account where user_id=?
		//构建查询条件
		QueryWrapper<UserAccount> queryWrapper=new QueryWrapper<>();
		//设置条件
		queryWrapper.eq("user_id",userId);
		//执行查询
		UserAccount userAccount = userAccountMapper.selectOne(queryWrapper);
		//判断
		if(userAccount!=null){
			return userAccount.getAvailableAmount();
		}

		return null;
	}

	/**
	 * 扣减账户金额
	 * @param accountLockVo
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void checkAndLock(AccountLockVo accountLockVo) {

		//扣减用户账户余额
		int count=userAccountMapper.checkAndDeduct(accountLockVo.getAmount(),accountLockVo.getUserId());

		//判读是否扣减成功
		if(count==0){

			throw new GuiguException(400,"账户扣减异常");
		}



		//新增账户记录
		this.saveUserAccountDetail(
				accountLockVo.getUserId(),
				accountLockVo.getContent(),
				SystemConstant.ACCOUNT_TRADE_TYPE_MINUS,
				accountLockVo.getAmount(),
				accountLockVo.getOrderNo());


	}

	/**
	 * 查询用户账户记录明细
	 * @param userAccountDetailPage
	 * @param userId
	 * @param accountTradeTypeDeposit
	 * @return
	 */
	@Override
	public Page<UserAccountDetail> getUserAccountDetailPage(Page<UserAccountDetail> userAccountDetailPage, Long userId, String tradeType) {

		//构建条件对象
		QueryWrapper<UserAccountDetail> queryWrapper=new QueryWrapper<>();
		// select*from user_account_detail where user_id=? and trade_type= tradeType order by id desc

		queryWrapper.eq("user_id", userId).eq("trade_type",tradeType).orderByDesc("id");

		//调用api查询
		userAccountDetailPage = userAccountDetailMapper.selectPage(userAccountDetailPage, queryWrapper);


		return userAccountDetailPage;
	}
}
