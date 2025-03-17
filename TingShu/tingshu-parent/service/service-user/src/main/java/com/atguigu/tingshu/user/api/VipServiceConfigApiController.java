package com.atguigu.tingshu.user.api;

import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.user.VipServiceConfig;
import com.atguigu.tingshu.user.service.VipServiceConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "VIP服务配置管理接口")
@RestController
@RequestMapping("api/user")
@SuppressWarnings({"all"})
public class VipServiceConfigApiController {

	@Autowired
	private VipServiceConfigService vipServiceConfigService;




	/**
	 *根据id获取VIP服务配置信息
	 * api/user/vipServiceConfig/getVipServiceConfig/{id}
	 * @param id
	 * @return
	 */
	@GetMapping("/vipServiceConfig/getVipServiceConfig/{id}")
	public Result<VipServiceConfig> getVipServiceConfig(@PathVariable Long id){


		return Result.ok(vipServiceConfigService.getById(id));
	}




	/**
	 * api/user/vipServiceConfig/findAll
	 * 获取全部VIP会员服务配置信息
	 * @return
	 */
	@GetMapping("/vipServiceConfig/findAll")
	public Result<List<VipServiceConfig>> findAll(){


		return Result.ok(vipServiceConfigService.list());
	}


}

