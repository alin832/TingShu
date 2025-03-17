package com.atguigu.tingshu.account.api;

import com.atguigu.tingshu.account.service.UserAccountService;
import com.atguigu.tingshu.common.constant.SystemConstant;
import com.atguigu.tingshu.common.login.GuiguLogin;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.model.account.UserAccountDetail;
import com.atguigu.tingshu.vo.account.AccountLockVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Tag(name = "用户账户管理")
@RestController
@RequestMapping("api/account")
@SuppressWarnings({"all"})
public class UserAccountApiController {

	@Autowired
	private UserAccountService userAccountService;




	/**
	 * api/account/userAccount/findUserRechargePage/{page}/{limit}
	 * 获取充值记录
	 * @param page
	 * @param limit
	 * @return
	 */
	@GetMapping("/userAccount/findUserRechargePage/{page}/{limit}")
	@GuiguLogin
	public Result<Page<UserAccountDetail>> findUserRechargePage(@PathVariable Long page,
									   @PathVariable Long limit ){

		//获取用户ID
		Long userId = AuthContextHolder.getUserId();
		//封装分页对象
		Page<UserAccountDetail> userAccountDetailPage = new Page<UserAccountDetail>(page, limit);
		//调用service
		userAccountDetailPage=userAccountService.getUserAccountDetailPage(userAccountDetailPage,userId, SystemConstant.ACCOUNT_TRADE_TYPE_DEPOSIT);

		return Result.ok(userAccountDetailPage);

	}

	/**
	 * /api/account/userAccount/findUserConsumePage/{page}/{limit}
	 * 获取消费记录
	 * @param page
	 * @param limit
	 * @return
	 */
	@GetMapping("/userAccount/findUserConsumePage/{page}/{limit}")
	@GuiguLogin
	public Result<Page<UserAccountDetail>> findUserConsumePage(@PathVariable Long page,
									   @PathVariable Long limit ){

		//获取用户ID
		Long userId = AuthContextHolder.getUserId();
		//封装分页对象
		Page<UserAccountDetail> userAccountDetailPage = new Page<UserAccountDetail>(page, limit);
		//调用service
		userAccountDetailPage=userAccountService.getUserAccountDetailPage(userAccountDetailPage,userId, SystemConstant.ACCOUNT_TRADE_TYPE_MINUS);

		return Result.ok(userAccountDetailPage);

	}





	/**
	 * 扣减账户金额
	 * api/account/userAccount/checkAndLock
	 * @param accountLockVo
	 * @return
	 */
	@PostMapping("/userAccount/checkAndLock")
	@GuiguLogin
	public Result checkAndDeduct(@RequestBody AccountLockVo accountLockVo){

		userAccountService.checkAndLock(accountLockVo);

		return Result.ok();
	}




	/**
	 * 获取账户可用余额
	 * api/account/userAccount/getAvailableAmount
	 * @return
	 */
	@GuiguLogin
	@GetMapping("/userAccount/getAvailableAmount")
	public Result<BigDecimal> getAvailableAmount(){

		//获取用户id
		Long userId = AuthContextHolder.getUserId();

		//获取当前用户可用余额
		BigDecimal bigDecimal=userAccountService.getAvailableAmount(userId);

		return Result.ok(bigDecimal);
	}


}

