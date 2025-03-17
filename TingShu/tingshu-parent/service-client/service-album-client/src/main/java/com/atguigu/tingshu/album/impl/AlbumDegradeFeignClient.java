package com.atguigu.tingshu.album.impl;


import com.atguigu.tingshu.album.AlbumFeignClient;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.album.*;
import com.atguigu.tingshu.vo.album.AlbumStatVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AlbumDegradeFeignClient implements AlbumFeignClient {


    /**
     * 查询声音信息
     * @param id
     * @return
     */
    @Override
    public Result<TrackInfo> getTrackInfo(Long id) {
        log.error("[专辑模块]提供远程调用getTrackInfo服务降级");
        return Result.fail();
    }

    /**
     * 根据声音ID+声音数量 获取下单付费声音列表
     * @param trackId
     * @param trackCount
     * @return
     */
    @Override
    public Result<List<TrackInfo>> findPaidTrackInfoList(Long trackId, Integer trackCount) {
        log.error("[专辑模块]提供远程调用findPaidTrackInfoList服务降级");
        return Result.fail();
    }

    /**
     * 查询所有的一级分类信息
     * @return
     */
    @Override
    public Result<List<BaseCategory1>> findAllCategory1() {
        log.error("[专辑模块]提供远程调用findAllCategory1服务降级");
        return Result.fail();
    }

    /**
     * 根据专辑ID获取专辑统计信息
     * @param albumId
     * @return
     */
    @Override
    public Result<AlbumStatVo> getAlbumStatVo(Long albumId) {
        log.error("[专辑模块]提供远程调用getAlbumStatVo服务降级");
        return Result.fail();
    }

    /**
     * 根据一级分类Id查询三级分类列表
     * @param category1Id
     * @return
     */
    @Override
    public Result<List<BaseCategory3>> findTopBaseCategory3(Long category1Id) {
        log.error("[专辑模块]提供远程调用findTopBaseCategory3服务降级");
        return Result.fail();
    }

    /**
     * 根据三级分类Id 获取到分类信息
     * @param category3Id
     * @return
     */
    @Override
    public Result<BaseCategoryView> getCategoryView(Long category3Id) {
        log.error("[专辑模块]提供远程调用getCategoryView服务降级");
        return Result.fail();
    }

    /**
     *  根据ID查询专辑信息
     * @param id
     * @return
     */
    @Override
    public Result<AlbumInfo> getAlbumInfo(Long id) {
        log.error("[专辑模块]提供远程调用getAlbumInfo服务降级");
        return Result.fail();
    }
}
