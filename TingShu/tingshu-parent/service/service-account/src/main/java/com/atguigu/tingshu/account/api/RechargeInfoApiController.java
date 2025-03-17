package com.atguigu.tingshu.account.api;

import com.atguigu.tingshu.account.service.RechargeInfoService;
import com.atguigu.tingshu.common.login.GuiguLogin;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.account.RechargeInfo;
import com.atguigu.tingshu.vo.account.RechargeInfoVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "充值管理")
@RestController
@RequestMapping("api/account")
@SuppressWarnings({"all"})
public class RechargeInfoApiController {

	@Autowired
	private RechargeInfoService rechargeInfoService;




	/**
	 * api/account/rechargeInfo/rechargePaySuccess/{orderNo}
	 * 支付成功后充值处理
	 * @param orderNo
	 * @return
	 */
	@GetMapping("/rechargeInfo/rechargePaySuccess/{orderNo}")
	public Result rechargePaySuccess(@PathVariable String orderNo){

		rechargeInfoService.rechargePaySuccess(orderNo);
		return Result.ok();
	}


	/**
	 * 充值金额
	 * api/account/rechargeInfo/submitRecharge
	 * @param rechargeInfoVo
	 * @return
	 */
	@PostMapping("/rechargeInfo/submitRecharge")
	@GuiguLogin
	public Result<Map<String,String>> submitRecharge(@RequestBody RechargeInfoVo rechargeInfoVo){

		Map<String,String> resultMap=rechargeInfoService.submitRecharge(rechargeInfoVo);



		return  Result.ok(resultMap);
	}



	/**
	 * 根据订单号获取充值信息
	 * api/account/rechargeInfo/getRechargeInfo/{orderNo}
	 * @param orderNo
	 * @return
	 */
	@GetMapping("/rechargeInfo/getRechargeInfo/{orderNo}")
	public Result<RechargeInfo> getRechargeInfo(@PathVariable String orderNo){

		RechargeInfo rechargeInfo=rechargeInfoService.getRechargeInfo(orderNo);

		return Result.ok(rechargeInfo);
	}



}

