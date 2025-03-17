package com.atguigu.tingshu.album.service;

import com.atguigu.tingshu.model.album.TrackInfo;
import com.atguigu.tingshu.query.album.TrackInfoQuery;
import com.atguigu.tingshu.vo.album.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface TrackInfoService extends IService<TrackInfo> {

    /**
     * 保存声音
     * @param trackInfoVo
     * @param userId
     */
    void saveTrackInfo(TrackInfoVo trackInfoVo, Long userId);

    /**
     * 初始化统计信息
     * @param trackId
     * @param statType
     * @param stateNum
     */
    void saveTrackState(Long trackId, String statType, int stateNum);

    /**
     * 获取当前用户声音分页列表
     * @param trackInfoQuery
     * @return
     */
    Page<TrackListVo> findUserTrackPage(Page<TrackListVo> listVoPage,TrackInfoQuery trackInfoQuery);

    /**
     * 修改声音信息
     * @param id
     * @param trackInfoVo
     */
    void updateTrackInfo(Long id, TrackInfoVo trackInfoVo);

    /**
     * 删除声音信息
     * @param id
     */
    void removeTrackInfo(Long id);

    /**
     * 查询专辑声音分页列表
     * @param albumTrackListVoPage
     * @param albumId
     * @return
     */
    Page<AlbumTrackListVo> findAlbumTrackPage(Page<AlbumTrackListVo> albumTrackListVoPage, Long albumId,Long userId);

    /**
     *  声音统计处理
     * @param trackStatMqVo
     */
    void updateTrackStat(TrackStatMqVo trackStatMqVo);

    /**
     * 获取声音统计信息
     * @param trackId
     * @return
     */
    TrackStatVo getTrackStatVo(Long trackId);

    /**
     * 获取用户声音分集购买支付列表
     *
     * @param trackId
     * @param userId
     * @return
     */
    List<Map<String, Object>> findUserTrackPaidList(Long trackId, Long userId);

    /**
     * 根据声音ID+声音数量 获取下单付费声音列表
     * @param trackId
     * @param trackCount
     * @return
     */
    List<TrackInfo> findPaidTrackInfoList(Long trackId, Integer trackCount);
}
