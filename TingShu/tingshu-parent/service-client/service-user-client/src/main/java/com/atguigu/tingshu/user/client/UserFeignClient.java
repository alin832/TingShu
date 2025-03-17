package com.atguigu.tingshu.user.client;

import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.user.VipServiceConfig;
import com.atguigu.tingshu.user.client.impl.UserDegradeFeignClient;
import com.atguigu.tingshu.vo.user.UserInfoVo;
import com.atguigu.tingshu.vo.user.UserPaidRecordVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 产品列表API接口
 * </p>
 *
 * @author atguigu
 */
@FeignClient(value = "service-user", path = "api/user",fallback = UserDegradeFeignClient.class)
public interface UserFeignClient {


    /**
     * 更新vip过期状态
     * @return
     */
    @GetMapping("/userInfo/updateUserVipStatus")
    public Result updateUserVipStatus();
    /**
     * api/user/userInfo/savePaidRecord
     * 新增购买记录
     * @param userPaidRecordVo
     * @return
     */
    @PostMapping("/userInfo/savePaidRecord")
    public Result savePaidRecord(@RequestBody UserPaidRecordVo userPaidRecordVo);

    /**
     * 根据专辑id+用户ID获取用户已购买声音id列表
     * api/user/userInfo/findUserPaidTrackList/{albumId}
     * @param albumId
     * @return
     */
    @GetMapping("/userInfo/findUserPaidTrackList/{albumId}")
    public Result<List<Long>> findUserPaidTrackList(@PathVariable Long albumId);

    /**
     * 判断用户是否购买过指定专辑
     * api/user/userInfo/isPaidAlbum/{albumId}
     * @param albumId
     * @return
     */
    @GetMapping("/userInfo/isPaidAlbum/{albumId}")
    public Result<Boolean> isPaidAlbum(@PathVariable Long albumId);

    /**
     *根据id获取VIP服务配置信息
     * api/user/vipServiceConfig/getVipServiceConfig/{id}
     * @param id
     * @return
     */
    @GetMapping("/vipServiceConfig/getVipServiceConfig/{id}")
    public Result<VipServiceConfig> getVipServiceConfig(@PathVariable Long id);
    /**
     * 获取用户声音列表付费情况
     * api/user/userInfo/userIsPaidTrack/{userId}/{albumId}
     * @param userId
     * @param albumId
     * @param needChackTrackIdList
     * @return
     */
    @PostMapping("/userInfo/userIsPaidTrack/{userId}/{albumId}")
    public Result<Map<Long,Integer>> userIsPaidTrack(@PathVariable Long userId,
                                                     @PathVariable Long albumId,
                                                     @RequestBody List<Long> needChackTrackIdList);
    /**
     * 根据用户ID查询用户信息
     * api/user/userInfo/getUserInfoVo/{userId}
     * @return
     */
    @GetMapping("/userInfo/getUserInfoVo/{userId}")
    public Result<UserInfoVo> getUserInfoVo(@PathVariable Long userId);


}
