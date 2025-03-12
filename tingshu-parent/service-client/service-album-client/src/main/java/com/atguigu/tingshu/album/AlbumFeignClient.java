package com.atguigu.tingshu.album;

import com.atguigu.tingshu.album.impl.AlbumDegradeFeignClient;
import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.model.album.*;
import com.atguigu.tingshu.vo.album.AlbumStatVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * <p>
 * 专辑模块远程调用Feign接口
 * </p>
 *
 * @author atguigu
 */
@FeignClient(value = "service-album",path = "/api/album",fallback = AlbumDegradeFeignClient.class)
public interface AlbumFeignClient {

    /**
     *查询声音信息
     * api/album/trackInfo/getTrackInfo/{id}
     * @param id
     * @return
     */
    @GetMapping("/trackInfo/getTrackInfo/{id}")
    public Result<TrackInfo>getTrackInfo(@PathVariable Long id );

    /**
     * 根据声音ID+声音数量 获取下单付费声音列表
     * api/album/trackInfo/findPaidTrackInfoList/{trackId}/{trackCount}
     * @param trackId
     * @param trackCount
     * @return
     */
    @GetMapping("/trackInfo/findPaidTrackInfoList/{trackId}/{trackCount}")
    public Result<List<TrackInfo>> findPaidTrackInfoList(@PathVariable Long trackId,
                                                         @PathVariable Integer trackCount);
    /**
     * 查询所有的一级分类信息
     * api/album/category/findAllCategory1
     * @return
     */
    @GetMapping("/category/findAllCategory1")
    public Result<List<BaseCategory1>> findAllCategory1();
    /**
     * 根据专辑ID获取专辑统计信息
     * api/album/albumInfo/getAlbumStatVo/{albumId}
     * @param albumId
     * @return
     */
    @GetMapping("/albumInfo/getAlbumStatVo/{albumId}")
    public Result<AlbumStatVo> getAlbumStatVo(@PathVariable Long albumId);

    /**
     * api/album/category/findTopBaseCategory3/{category1Id}
     * 根据一级分类Id查询三级分类列表
     * @param category1Id
     * @return
     */
    @GetMapping("/category/findTopBaseCategory3/{category1Id}")
    public Result<List<BaseCategory3>> findTopBaseCategory3(@PathVariable Long category1Id);

    /**
     * api/album/category/getCategoryView/{category3Id}
     * 根据三级分类Id 获取到分类信息
     * @param category3Id
     * @return
     */
    @GetMapping("/category/getCategoryView/{category3Id}")
    public Result<BaseCategoryView> getCategoryView(@PathVariable Long category3Id );

    /**
     * 根据ID查询专辑信息
     * /api/album/albumInfo/getAlbumInfo/{id}
     * @param id
     * @return
     */
    @GetMapping("/albumInfo/getAlbumInfo/{id}")
    public Result<AlbumInfo> getAlbumInfo(@PathVariable Long id);


}
