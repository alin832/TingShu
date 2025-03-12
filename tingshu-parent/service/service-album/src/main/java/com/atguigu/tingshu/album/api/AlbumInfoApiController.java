package com.atguigu.tingshu.album.api;

import com.atguigu.tingshu.album.service.AlbumInfoService;
import com.atguigu.tingshu.common.login.GuiguLogin;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.model.album.AlbumInfo;
import com.atguigu.tingshu.query.album.AlbumInfoQuery;
import com.atguigu.tingshu.vo.album.AlbumInfoVo;
import com.atguigu.tingshu.vo.album.AlbumListVo;
import com.atguigu.tingshu.vo.album.AlbumStatVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tomcat.util.descriptor.web.ContextHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "专辑管理")
@RestController
@RequestMapping("api/album")
@SuppressWarnings({"all"})
public class AlbumInfoApiController {

	@Autowired
	private AlbumInfoService albumInfoService;




	/**
	 * 根据专辑ID获取专辑统计信息
	 * api/album/albumInfo/getAlbumStatVo/{albumId}
	 * @param albumId
	 * @return
	 */
	@GetMapping("/albumInfo/getAlbumStatVo/{albumId}")
	public Result<AlbumStatVo> getAlbumStatVo(@PathVariable Long albumId){

		AlbumStatVo albumStatVo=albumInfoService.getAlbumStatVo(albumId);


		return Result.ok(albumStatVo);
	}




	/**
	 * 获取当前用户全部专辑列表
	 * api/album/albumInfo/findUserAllAlbumList
	 * @return
	 */
	@GetMapping("/albumInfo/findUserAllAlbumList")
	@GuiguLogin
	public Result<List<AlbumInfo>> findUserAllAlbumList(){

		//获取用户ID
		Long userId = AuthContextHolder.getUserId();
		//调用查询
		List<AlbumInfo>  albumInfoList=albumInfoService.findUserAllAlbumList(userId);
		return Result.ok(albumInfoList);
	}


	/**
	 * 修改专辑
	 * api/album/albumInfo/updateAlbumInfo/{id}
	 * @param albumInfoVo
	 * @param id
	 * @return
	 */
	@PutMapping("/albumInfo/updateAlbumInfo/{id}")
	public Result updateAlbumInfo(@RequestBody AlbumInfoVo albumInfoVo,
								  @PathVariable Long id ){

		albumInfoService.updateAlbumInfo(albumInfoVo,id);



		return Result.ok();

	}


	/**
	 * 根据ID查询专辑信息
	 * /api/album/albumInfo/getAlbumInfo/{id}
	 * @param id
	 * @return
	 */
	@GetMapping("/albumInfo/getAlbumInfo/{id}")
	public Result<AlbumInfo> getAlbumInfo(@PathVariable Long id){
		AlbumInfo albumInfo=albumInfoService.getAlbumInfo(id);

		return Result.ok(albumInfo);
	}

	/**
	 * 根据ID删除专辑
	 * api/album/albumInfo/removeAlbumInfo/{id}
	 * @param id
	 * @return
	 */
	@DeleteMapping("/albumInfo/removeAlbumInfo/{id}")
	public Result removeAlbumInfo(@PathVariable Long id){

		albumInfoService.removeAlbumInfo(id);

		return Result.ok();
	}



	/**
	 * api/album/albumInfo/findUserAlbumPage/{page}/{limit}
	 * 查看当前用户专辑分页列表
	 * @param page
	 * @param limit
	 * @return
	 */
	@PostMapping("/albumInfo/findUserAlbumPage/{page}/{limit}")
	@GuiguLogin(required = true)
	public Result<Page<AlbumListVo>> findUserAlbumPage(@PathVariable Long page,
													   @PathVariable Long limit,
													   @RequestBody AlbumInfoQuery albumInfoQuery){





		//封装分页对象
		Page<AlbumListVo> albumListVoPage=new Page<>(page,limit);

		//获取用户ID
		Long userId = AuthContextHolder.getUserId();
		//封装查询条件对象
		albumInfoQuery.setUserId(userId);
		//调用service
		albumListVoPage=albumInfoService.findUserAlbumPage(albumListVoPage,albumInfoQuery);


		return Result.ok(albumListVoPage);
	}






	/**
	 * 新增专辑
	 * api/album/albumInfo/saveAlbumInfo
	 * @param albumInfoVo
	 * @return
	 */
	@PostMapping("/albumInfo/saveAlbumInfo")
	@GuiguLogin
	public Result saveAlbumInfo(@RequestBody AlbumInfoVo albumInfoVo){

		//获取用户ID
		Long userId = AuthContextHolder.getUserId();

		albumInfoService.saveAlbumInfo(albumInfoVo,userId);


		return Result.ok();
	}

}

