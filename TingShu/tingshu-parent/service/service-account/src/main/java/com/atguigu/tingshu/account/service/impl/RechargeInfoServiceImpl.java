package com.atguigu.tingshu.account.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.atguigu.tingshu.account.mapper.RechargeInfoMapper;
import com.atguigu.tingshu.account.mapper.UserAccountMapper;
import com.atguigu.tingshu.account.service.RechargeInfoService;
import com.atguigu.tingshu.account.service.UserAccountService;
import com.atguigu.tingshu.common.constant.SystemConstant;
import com.atguigu.tingshu.common.execption.GuiguException;
import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.model.account.RechargeInfo;
import com.atguigu.tingshu.vo.account.RechargeInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@SuppressWarnings({"all"})
public class RechargeInfoServiceImpl extends ServiceImpl<RechargeInfoMapper, RechargeInfo> implements RechargeInfoService {

	@Autowired
	private RechargeInfoMapper rechargeInfoMapper;

	@Autowired
	private UserAccountService userAccountService;

	/**
	 * 根据订单号获取充值信息
	 * @param orderNo
	 * @return
	 */
	@Override
	public RechargeInfo getRechargeInfo(String orderNo) {


		QueryWrapper<RechargeInfo> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("order_no",orderNo);

		RechargeInfo rechargeInfo = rechargeInfoMapper.selectOne(queryWrapper);
		return rechargeInfo;
	}

	/**
	 * 充值金额
	 * @param rechargeInfoVo
	 * @return
	 */
	@Override
	public Map<String, String> submitRecharge(RechargeInfoVo rechargeInfoVo) {

		RechargeInfo rechargeInfo = new RechargeInfo();

		Long userId = AuthContextHolder.getUserId();
		rechargeInfo.setUserId(userId);
		String orderNo="CZ"+ DateUtil.today().replaceAll("-","")+ IdUtil.getSnowflakeNextId();
		rechargeInfo.setOrderNo(orderNo);
		rechargeInfo.setRechargeStatus(SystemConstant.ORDER_STATUS_UNPAID);
		rechargeInfo.setRechargeAmount(rechargeInfoVo.getAmount());
		rechargeInfo.setPayWay(rechargeInfoVo.getPayWay());
		rechargeInfoMapper.insert(rechargeInfo);

		//创建集合封装数据
		Map<String,String> resultMap=new HashMap<>();
		resultMap.put("orderNo",orderNo);
		return resultMap;
	}


	@Autowired
	private UserAccountMapper userAccountMapper;
	/**
	 * 支付成功后充值处理
	 * @param orderNo
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void rechargePaySuccess(String orderNo) {

		//查询充值记录
		QueryWrapper<RechargeInfo> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("order_no",orderNo);
		RechargeInfo rechargeInfo = rechargeInfoMapper.selectOne(queryWrapper);
		//判读那
		if(!SystemConstant.ORDER_STATUS_UNPAID.equals(rechargeInfo.getRechargeStatus())){

			return;
		}
		//修改充值状态
		rechargeInfo.setRechargeStatus(SystemConstant.ORDER_STATUS_PAID);
		rechargeInfo.setUpdateTime(new Date());
		rechargeInfoMapper.updateById(rechargeInfo);

		//更新账户金额
		int count =userAccountMapper.updateUserAccoutnAmount(rechargeInfo.getUserId(),rechargeInfo.getRechargeAmount());

		//判断
		if(count==0){
			throw new GuiguException(500,"账户更新异常");
		}



		//记录账户变更
		userAccountService.saveUserAccountDetail(
				rechargeInfo.getUserId(),
				"【账户充值】充值金额："+rechargeInfo.getRechargeAmount(),
				SystemConstant.ACCOUNT_TRADE_TYPE_DEPOSIT,
				rechargeInfo.getRechargeAmount(),
				rechargeInfo.getOrderNo());






	}
}
