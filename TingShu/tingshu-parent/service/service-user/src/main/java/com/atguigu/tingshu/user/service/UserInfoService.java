package com.atguigu.tingshu.user.service;

import com.atguigu.tingshu.model.user.UserInfo;
import com.atguigu.tingshu.vo.user.UserInfoVo;
import com.atguigu.tingshu.vo.user.UserPaidRecordVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {

    /**
     * 小程序授权登录
     * @param code
     * @return
     */
    Map<String, String> wxLogin(String code);

    /**
     * 获取登录用户信息
     * @param userId
     * @return
     */
    UserInfoVo getUserInfo(Long userId);

    /**
     * 更新用户信息
     * @param userInfoVo
     */
    void updateUser(UserInfoVo userInfoVo,String token);

    /**
     *  获取用户声音列表付费情况
     * @param userId
     * @param albumId
     * @param needChackTrackIdList
     * @return
     */
    Map<Long, Integer> userIsPaidTrack(Long userId, Long albumId, List<Long> needChackTrackIdList);

    /**
     * 判断用户是否购买过指定专辑
     * @param userId
     * @param albumId
     * @return
     */
    Boolean isPaidAlbum(Long userId, Long albumId);

    /**
     * 根据专辑id+用户ID获取用户已购买声音id列表
     * @param albumId
     * @param userId
     * @return
     */
    List<Long> findUserPaidTrackList(Long albumId, Long userId);

    /**
     * 新增购买记录
     * @param userPaidRecordVo
     */
    void savePaidRecord(UserPaidRecordVo userPaidRecordVo);

    /**
     * 更新vip过期状态
     */
    void updateUserVipStatus();


}
