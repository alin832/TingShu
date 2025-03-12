package com.atguigu.tingshu.user.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.atguigu.tingshu.album.AlbumFeignClient;
import com.atguigu.tingshu.common.constant.KafkaConstant;
import com.atguigu.tingshu.common.constant.RedisConstant;
import com.atguigu.tingshu.common.constant.SystemConstant;
import com.atguigu.tingshu.common.service.KafkaService;
import com.atguigu.tingshu.model.album.TrackInfo;
import com.atguigu.tingshu.model.user.*;
import com.atguigu.tingshu.user.client.UserFeignClient;
import com.atguigu.tingshu.user.mapper.*;
import com.atguigu.tingshu.user.service.UserInfoService;
import com.atguigu.tingshu.user.startagy.ItemTypeStrategy;
import com.atguigu.tingshu.user.startagy.StrategyFactory;
import com.atguigu.tingshu.vo.user.UserInfoVo;
import com.atguigu.tingshu.vo.user.UserPaidRecordVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressWarnings({"all"})
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

	@Autowired
	private UserInfoMapper userInfoMapper;

	@Autowired
	private WxMaService wxMaService;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private KafkaService kafkaService;

	/**
	 * 小程序授权登录
	 * @param code
	 * @return
	 */
	@Override
	public Map<String, String> wxLogin(String code) {
		Map<String, String> resultMap= null;
		try {
			//创建封装响应对象
			resultMap = new HashMap<>();
			//调用微信认证接口 GET https://api.weixin.qq.com/sns/jscode2session
			WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
			//判断
			if(sessionInfo!=null){
				//获取认证的openId
				String openid = sessionInfo.getOpenid();
				//构建查询条件对象
				LambdaQueryWrapper<UserInfo> queryWrapper= new LambdaQueryWrapper<>();
				queryWrapper.eq(UserInfo::getWxOpenId,openid);
				//根据openid查询用户信息
				//select*from user_info where wx_open_id=?
				UserInfo userInfo = userInfoMapper.selectOne(queryWrapper);
				//判断是否注册还是登录
				if(userInfo==null){//此时注册
					//1.初始化用户信息
					userInfo=new UserInfo();
					userInfo.setWxOpenId(openid);
					userInfo.setNickname("听友:"+ IdUtil.fastUUID());
					userInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
					userInfo.setIsVip(0);
					//2.保存用户信息
					userInfoMapper.insert(userInfo);
					//3.初始化用户账户
					kafkaService.sendMessage(KafkaConstant.QUEUE_USER_REGISTER,userInfo.getId().toString());

				}


				//1.生成token
				String token=IdUtil.getSnowflakeNextIdStr();

				//2.定义存储key
				String loginKey= RedisConstant.USER_LOGIN_KEY_PREFIX+token;
				//基于对安全信息控制，UserInfoVo
				UserInfoVo userInfoVo = BeanUtil.copyProperties(userInfo, UserInfoVo.class);


				//3.存储用户信息到redis
				redisTemplate.opsForValue().set(loginKey,userInfoVo,RedisConstant.USER_LOGIN_KEY_TIMEOUT, TimeUnit.SECONDS);
				//4.封装token，响应到前端

				resultMap.put("token",token);
			}
		} catch (WxErrorException e) {
			log.error("[用户服务]微信登录异常：{}", e);
			throw new RuntimeException(e);
		}


		return resultMap;

	}

	/**
	 * 获取登录用户信息
	 * @param userId
	 * @return
	 */
	@Override

	public UserInfoVo getUserInfo(Long userId) {
		//获取用户信息
		UserInfo userInfo = userInfoMapper.selectById(userId);

		return BeanUtil.copyProperties(userInfo,UserInfoVo.class);
	}

	/**
	 *  更新用户信息
	 * @param userInfoVo
	 */
	@Override
	public void updateUser(UserInfoVo userInfoVo,String token) {

		//转换类型
		UserInfo userInfo = BeanUtil.copyProperties(userInfoVo, UserInfo.class);

		userInfoMapper.updateById(userInfo);

		//获取最新数据
		UserInfoVo user = this.getUserInfo(userInfoVo.getId());

		String loginKey =RedisConstant.USER_LOGIN_KEY_PREFIX+token;

		redisTemplate.opsForValue().set(loginKey,user,RedisConstant.USER_LOGIN_KEY_TIMEOUT,TimeUnit.SECONDS);
	}


	@Autowired
	private UserPaidAlbumMapper userPaidAlbumMapper;
	@Autowired
	private UserPaidTrackMapper userPaidTrackMapper;

	/**
	 *  获取用户声音列表付费情况
	 * @param userId
	 * @param albumId
	 * @param needChackTrackIdList
	 * @return
	 */
	@Override
	public Map<Long, Integer> userIsPaidTrack(Long userId, Long albumId, List<Long> needChackTrackIdList) {
		//创建Map封装结果
		Map<Long, Integer> resultMap=new HashMap<>();
		//构建查询专辑购买条件
		//select*from user_paid_album where userid=? and albumid=?
		QueryWrapper<UserPaidAlbum> userPaidAlbumQueryWrapper=new QueryWrapper<>();
		userPaidAlbumQueryWrapper.eq("user_id",userId);
		userPaidAlbumQueryWrapper.eq("album_id",albumId);

		//根据专辑ID查询用户该买的专辑
		Long count = userPaidAlbumMapper.selectCount(userPaidAlbumQueryWrapper);

		//购买了专辑，直接所有声音ID列表都设置为1，表示都已购买
		if(count.intValue()>0){
			for (Long trackId : needChackTrackIdList) {
				resultMap.put(trackId,1);
			}
			return resultMap;
		}


		//构建查询结果
		QueryWrapper<UserPaidTrack> trackQueryWrapper=new QueryWrapper<>();
		trackQueryWrapper.eq("user_id",userId);
		trackQueryWrapper.in("track_id",needChackTrackIdList);
		//select*from user_paid_track where user_id=? and track_id in (1,2,3,4,5)
		//根据声音列表查询列表中包含的声音是否有购买
		List<UserPaidTrack> userPaidTracks = userPaidTrackMapper.selectList(trackQueryWrapper);

		//没有查询到，当前用户对于专辑和声音没有购买情况 设置0

		if(CollectionUtil.isEmpty(userPaidTracks)){

			for (Long trackId : needChackTrackIdList) {
				resultMap.put(trackId,0);
			}
			return resultMap;

		}

		//获取查询到的已经购买的声音ID集合
		List<Long> userPaidTrackIdList = userPaidTracks.stream().map(userPaidTrack -> userPaidTrack.getTrackId()).collect(Collectors.toList());

		//有查询到结果，判断哪些声音有购买记录设置为1，没有购买记录的设置为0

		for (Long trackId : needChackTrackIdList) {

			//判断购买的id列表中是否包含待验证的id
			if(userPaidTrackIdList.contains(trackId)){
				//包含--购买
				resultMap.put(trackId,1);

			}else{

				resultMap.put(trackId,0);
			}


		}


		return resultMap;
	}

	/**
	 * 判断用户是否购买过指定专辑
	 * @param userId
	 * @param albumId
	 * @return
	 */
	@Override
	public Boolean isPaidAlbum(Long userId, Long albumId) {

		//select count(*) from user_paid_album where user_id=? and album_id=?
		//构建查询条件
		LambdaQueryWrapper<UserPaidAlbum> queryWrapper=new LambdaQueryWrapper<>();
		queryWrapper.eq(UserPaidAlbum::getUserId,userId);
		queryWrapper.eq(UserPaidAlbum::getAlbumId,albumId);

		//查询
		return userPaidAlbumMapper.selectCount(queryWrapper)>0;


	}

	/**
	 * 根据专辑id+用户ID获取用户已购买声音id列表
	 * @param albumId
	 * @param userId
	 * @return
	 */
	@Override
	public List<Long> findUserPaidTrackList(Long albumId, Long userId) {

		//select*from user_paid_track where userid=? and album_id=?

		//构建插叙条件
		QueryWrapper<UserPaidTrack> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("user_id",userId);
		queryWrapper.eq("album_id",albumId);
		//查询
		List<UserPaidTrack> userPaidTracks = userPaidTrackMapper.selectList(queryWrapper);
		//判断
		if(CollectionUtil.isNotEmpty(userPaidTracks)){

			List<Long> trackIdList = userPaidTracks.stream().map(userPaidTrack -> {

				return userPaidTrack.getTrackId();
			}).collect(Collectors.toList());

			return trackIdList;

		}


		return null;
	}


	@Autowired
	private AlbumFeignClient albumFeignClient;

	@Autowired
	private UserVipServiceMapper userVipServiceMapper;

	@Autowired
	private UserFeignClient userFeignClient;

	@Autowired
	private VipServiceConfigMapper vipServiceConfigMapper;


	@Autowired
	private StrategyFactory strategyFactory;

	/**
	 * 新增购买记录
	 * @param userPaidRecordVo
	 *
	 * 项目中提供的购买方式：
	 *  1.购买vip
	 *  	 user_info
	 * 		 user_vip_service
	 *  2.购买专辑
	 *      user_paid_album
	 *  3.购买声音
	 *     user_paid_track
	 *
	 * 根据：获取用户购买的项目
	 *     UserPaidRecordVo-itemType
	 *     对比
	 *  1001-专辑 1002-声音 1003-vip会员
	 *     public static final String  ORDER_ITEM_TYPE_ALBUM="1001";  // 专辑
	 *     public static final String  ORDER_ITEM_TYPE_TRACK="1002";  // 声音
	 *     public static final String  ORDER_ITEM_TYPE_VIP="1003";  // vip会员
	 *
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void savePaidRecord(UserPaidRecordVo userPaidRecordVo) {

		//从策略工程获取指定的策略类型
		ItemTypeStrategy strategy = strategyFactory.getStrategy(userPaidRecordVo.getItemType());
		//调用新增购买记录处理实现
		strategy.savePaidRecord(userPaidRecordVo);


//		//获取用户购买的类型
//		String itemType = userPaidRecordVo.getItemType();
//		//判断
//		if(SystemConstant.ORDER_ITEM_TYPE_ALBUM.equals(itemType)){
//			//新增专辑记录-user_paid_album
//
//			//判断是否已经新增过记录
//			QueryWrapper<UserPaidAlbum> queryWrapper=new QueryWrapper<>();
//			queryWrapper.eq("order_no",userPaidRecordVo.getOrderNo());
//			//执行查询
//			Long count = userPaidAlbumMapper.selectCount(queryWrapper);
//			if(count>0){
//				return;
//			}
//
//			//新增专辑购买记录
//			UserPaidAlbum userPaidAlbum=new UserPaidAlbum();
//			userPaidAlbum.setOrderNo(userPaidRecordVo.getOrderNo());
//			userPaidAlbum.setUserId(userPaidRecordVo.getUserId());
//			userPaidAlbum.setAlbumId(userPaidRecordVo.getItemIdList().get(0));
//
//
//			userPaidAlbumMapper.insert(userPaidAlbum);
//
//
//		} else if(SystemConstant.ORDER_ITEM_TYPE_TRACK.equals(itemType)){
//			//新增声音记录-user_paid_track
//
//			QueryWrapper<UserPaidTrack> queryWrapper=new QueryWrapper<>();
//			queryWrapper.eq("order_no",userPaidRecordVo.getOrderNo());
//			//执行查询
//			Long count = userPaidTrackMapper.selectCount(queryWrapper);
//			//判断
//			if(count>0){
//				return;
//			}
//
//			//查询声音详情
//			TrackInfo trackInfo = albumFeignClient.getTrackInfo(userPaidRecordVo.getItemIdList().get(0)).getData();
//			//新增声音记录
//			userPaidRecordVo.getItemIdList().forEach(trackId->{
//
//				//新增声音购买记录
//				UserPaidTrack userPaidTrack = new UserPaidTrack();
//				userPaidTrack.setOrderNo(userPaidRecordVo.getOrderNo());
//				userPaidTrack.setUserId(userPaidRecordVo.getUserId());
//				userPaidTrack.setAlbumId(trackInfo.getAlbumId());
//				userPaidTrack.setTrackId(trackId);
//
//				userPaidTrackMapper.insert(userPaidTrack);
//
//			});
//
//
//		}else if(SystemConstant.ORDER_ITEM_TYPE_VIP.equals(itemType)){
//			//新增vip记录和处理vip业务
//			QueryWrapper<UserVipService> queryWrapper=new QueryWrapper<>();
//			queryWrapper.eq("order_no",userPaidRecordVo.getOrderNo());
//			//执行查询
//			Long count = userVipServiceMapper.selectCount(queryWrapper);
//			//判断
//			if(count>0){
//				return;
//			}
//			//添加vip服务购买记录
//			UserVipService userVipService = new UserVipService();
//			userVipService.setOrderNo(userPaidRecordVo.getOrderNo());
//			userVipService.setUserId(userPaidRecordVo.getUserId());
//
//			//根据用户ID查询用户信息
//			UserInfo userInfoVo = userInfoMapper.selectById(userPaidRecordVo.getUserId());
//			//获取vip状态字段
//			Integer isVip = userInfoVo.getIsVip();
//			//获取vip过期时间
//			Date vipExpireTime = userInfoVo.getVipExpireTime();
//
//			//根据本地购买套餐ID，查询套餐详情
//			VipServiceConfig serviceConfig = vipServiceConfigMapper.selectById(userPaidRecordVo.getItemIdList().get(0));
//			//获取套餐的服务月数
//			Integer month = serviceConfig.getServiceMonth();
//
//			//判断
//			if(isVip.intValue()==1&&vipExpireTime.after(new Date())){
//				//vip 本次购买开始时间为当前vip的过期时间
//				userVipService.setStartTime(vipExpireTime);
//				//设置本次购买vip的过期时间=上次过期时间+套餐的时间
//				userVipService.setExpireTime(DateUtil.offsetMonth(vipExpireTime, month));
//
//			}else{
//				//普通用户或者过期的vip
//				//开始时间
//				userVipService.setStartTime(new Date());
//				//结束实现
//				userVipService.setExpireTime(DateUtil.offsetMonth(new Date(), month));
//			}
//
//
//			userVipServiceMapper.insert(userVipService);
//
//			//更新user_info
//
//			userInfoVo.setIsVip(1);
//			userInfoVo.setVipExpireTime(userVipService.getExpireTime());
//			userInfoVo.setUpdateTime(new Date());
//			this.updateById(userInfoVo);
//
//
//		}


	}

	/**
	 * 更新vip过期状态
	 */
	@Override
	public void updateUserVipStatus() {

		//select*from user_info is_vip=1 and vip_expire_time< now()

		//构建条件查询对象
		QueryWrapper<UserInfo> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("is_vip",1);
		queryWrapper.lt("vip_expire_time",DateUtil.beginOfDay(new Date()));
		//执行查询
		List<UserInfo> userInfos = userInfoMapper.selectList(queryWrapper);
		//判断
		if(CollectionUtil.isNotEmpty(userInfos)){

			userInfos.forEach(userInfo -> {


				userInfo.setIsVip(0);
				userInfo.setUpdateTime(new Date());
				userInfoMapper.updateById(userInfo);

			});


		}


	}



}
