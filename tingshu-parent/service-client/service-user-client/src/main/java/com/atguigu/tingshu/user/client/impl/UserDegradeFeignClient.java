package com.atguigu.tingshu.user.client.impl;


import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.user.VipServiceConfig;
import com.atguigu.tingshu.user.client.UserFeignClient;
import com.atguigu.tingshu.vo.user.UserInfoVo;
import com.atguigu.tingshu.vo.user.UserPaidRecordVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserDegradeFeignClient implements UserFeignClient {

    /**
     * 更新vip过期状态
     * @return
     */
    @Override
    public Result updateUserVipStatus() {
        log.error("调用用户微服务的updateUserVipStatus方法降级");
        return Result.fail();
    }

    /**
     * 新增购买记录
     * @param userPaidRecordVo
     * @return
     */
    @Override
    public Result savePaidRecord(UserPaidRecordVo userPaidRecordVo) {
        log.error("调用用户微服务的savePaidRecord方法降级");
        return Result.fail();
    }

    /**
     * 根据专辑id+用户ID获取用户已购买声音id列表
     * @param albumId
     * @return
     */
    @Override
    public Result<List<Long>> findUserPaidTrackList(Long albumId) {
        log.error("调用用户微服务的findUserPaidTrackList方法降级");
        return Result.fail();
    }

    /**
     * 判断用户是否购买过指定专辑
     * @param albumId
     * @return
     */
    @Override
    public Result<Boolean> isPaidAlbum(Long albumId) {
        log.error("调用用户微服务的isPaidAlbum方法降级");
        return Result.fail();
    }

    /**
     * 根据id获取VIP服务配置信息
     * @param id
     * @return
     */
    @Override
    public Result<VipServiceConfig> getVipServiceConfig(Long id) {
        log.error("调用用户微服务的getVipServiceConfig方法降级");
        return Result.fail();
    }

    /**
     * 获取用户声音列表付费情况
     * @param userId
     * @param albumId
     * @param needChackTrackIdList
     * @return
     */
    @Override
    public Result<Map<Long, Integer>> userIsPaidTrack(Long userId, Long albumId, List<Long> needChackTrackIdList) {
        log.error("调用用户微服务的userIsPaidTrack方法降级");
        return Result.fail();
    }

    /**
     * 根据用户ID查询用户信息
     * @param userId
     * @return
     */
    @Override
    public Result<UserInfoVo> getUserInfoVo(Long userId) {
        log.error("调用用户微服务的getUserInfoVo方法降级");
        return Result.fail();
    }
}
