package com.atguigu.tingshu.album.api;

import com.atguigu.tingshu.album.service.TrackInfoService;
import com.atguigu.tingshu.album.service.VodService;
import com.atguigu.tingshu.common.login.GuiguLogin;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.common.util.AuthContextHolder;
import com.atguigu.tingshu.model.album.TrackInfo;
import com.atguigu.tingshu.query.album.TrackInfoQuery;
import com.atguigu.tingshu.vo.album.AlbumTrackListVo;
import com.atguigu.tingshu.vo.album.TrackInfoVo;
import com.atguigu.tingshu.vo.album.TrackListVo;
import com.atguigu.tingshu.vo.album.TrackStatVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.simpleframework.xml.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "声音管理")
@RestController
@RequestMapping("api/album")
@SuppressWarnings({"all"})
public class TrackInfoApiController {

	@Autowired
	private TrackInfoService trackInfoService;

	@Autowired
	private VodService vodService;


	/**
	 * 根据声音ID+声音数量 获取下单付费声音列表
	 * api/album/trackInfo/findPaidTrackInfoList/{trackId}/{trackCount}
	 * @param trackId
	 * @param trackCount
	 * @return
	 */
	@GetMapping("/trackInfo/findPaidTrackInfoList/{trackId}/{trackCount}")
	public Result<List<TrackInfo>> findPaidTrackInfoList(@PathVariable Long trackId,
														 @PathVariable Integer trackCount){

		List<TrackInfo> trackInfoList=trackInfoService.findPaidTrackInfoList(trackId,trackCount);

		return Result.ok(trackInfoList);

	}



	/**
	 * 获取用户声音分集购买支付列表
	 * api/album/trackInfo/findUserTrackPaidList/{trackId}
	 * Map<String, Object> map = new HashMap<>();
	 * 		map.put("name","本集"); // 显示文本
	 * 		map.put("price",albumInfo.getPrice()); // 专辑声音对应的价格
	 * 		map.put("trackCount",1); // 记录购买集数
	 * 		list.add(map);
	 */
	@GetMapping("/trackInfo/findUserTrackPaidList/{trackId}")
	@GuiguLogin(required = false)
	public Result<List<Map<String, Object>>> findUserTrackPaidList(@PathVariable Long trackId){


		//获取用户id
		Long userId = AuthContextHolder.getUserId();

		//调用service处理业务
		List<Map<String, Object>> trackListMap=trackInfoService.findUserTrackPaidList(trackId,userId);

		return Result.ok(trackListMap);
	}


	/**
	 *获取声音统计信息
	 * api/album/trackInfo/getTrackStatVo/{trackId}
	 * @param trackId
	 * @return
	 */
	@GetMapping("/trackInfo/getTrackStatVo/{trackId}")
	public Result<TrackStatVo> getTrackStatVo(@PathVariable Long trackId){

		TrackStatVo trackStatVo=trackInfoService.getTrackStatVo(trackId);

		return Result.ok(trackStatVo);
	}



	/**
	 * api/album/trackInfo/findAlbumTrackPage/{albumId}/{page}/{limit}
	 * 查询专辑声音分页列表
	 * @param albumId
	 * @param page
	 * @param limit
	 * @return
	 */
	@GetMapping("/trackInfo/findAlbumTrackPage/{albumId}/{page}/{limit}")
	@GuiguLogin(required = false)
	public Result<Page<AlbumTrackListVo>> findAlbumTrackPage(@PathVariable Long albumId,
															 @PathVariable Long page,
															 @PathVariable Long limit){

		//获取用户id
		Long userId = AuthContextHolder.getUserId();

		//封装分页对象
		Page<AlbumTrackListVo> albumTrackListVoPage = new Page<>(page,limit);
		//调用service
		albumTrackListVoPage=trackInfoService.findAlbumTrackPage(albumTrackListVoPage,albumId,userId);

		return Result.ok(albumTrackListVoPage);
	}



	/**
	 * 删除声音信息
	 * api/album/trackInfo/removeTrackInfo/{id}
	 * @param id
	 * @return
	 */
	@DeleteMapping("/trackInfo/removeTrackInfo/{id}")
	public Result removeTrackInfo(@PathVariable Long id ){
		trackInfoService.removeTrackInfo(id);

		return Result.ok();
	}


	/**
	 *修改声音信息
	 * api/album/trackInfo/updateTrackInfo/{id}
	 * @param id
	 * @param trackInfoVo
	 * @return
	 */
	@PutMapping("/trackInfo/updateTrackInfo/{id}")

	public Result updateTrackInfo(@PathVariable Long id,
								  @RequestBody TrackInfoVo trackInfoVo){


		trackInfoService.updateTrackInfo(id,trackInfoVo);

		return Result.ok();
	}


	/**
	 *查询声音信息
	 * api/album/trackInfo/getTrackInfo/{id}
	 * @param id
	 * @return
	 */
	@GetMapping("/trackInfo/getTrackInfo/{id}")
	public Result<TrackInfo>getTrackInfo(@PathVariable Long id ){



		return Result.ok(trackInfoService.getById(id));
	}


	/**
	 * 获取当前用户声音分页列表
	 * api/album/trackInfo/findUserTrackPage/{page}/{limit}
	 * @param page
	 * @param limit
	 * @param trackInfoQuery
	 * @return
	 */
	@PostMapping("/trackInfo/findUserTrackPage/{page}/{limit}")
	@GuiguLogin
	public Result<Page<TrackListVo>> findUserTrackPage(@PathVariable Long page,
													   @PathVariable Long limit,
													   @RequestBody TrackInfoQuery trackInfoQuery){

		//封装分页查询对象
		Page<TrackListVo> listVoPage=new Page<>(page,limit);
		//封装用户id
		Long userId = AuthContextHolder.getUserId();
		trackInfoQuery.setUserId(userId);
		//调用service
		listVoPage=trackInfoService.findUserTrackPage(listVoPage,trackInfoQuery);


		return Result.ok(listVoPage);
	}

	/**
	 * 保存声音
	 * api/album/trackInfo/saveTrackInfo
	 * @param trackInfoVo
	 * @return
	 *
	 */
	@PostMapping("/trackInfo/saveTrackInfo")
	@GuiguLogin
	public Result saveTrackInfo(@RequestBody TrackInfoVo trackInfoVo){

		//获取用户id
		Long userId = AuthContextHolder.getUserId();

		//调用service
		trackInfoService.saveTrackInfo(trackInfoVo,userId);


		return Result.ok();
	}

	/**
	 * 上传声音
	 * api/album/trackInfo/uploadTrack
	 * @param file
	 * @return
	 */
	@PostMapping("/trackInfo/uploadTrack")
	public Result<Map<String,String>> uploadTrack(MultipartFile file){

		Map<String,String> resultMap=	vodService.uploadTrack(file);

		return Result.ok(resultMap);
	}

}

