package com.atguigu.tingshu;

import com.atguigu.tingshu.common.result.Result;
import com.atguigu.tingshu.impl.SearchDegradeFeignClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @date: 2024/6/29 16:13
 * @author: yz
 * @version: 1.0
 */
@FeignClient(value = "service-search",  path = "api/search",fallback = SearchDegradeFeignClient.class)
public interface SearchFeignClient {


    /**
     * 更新排行榜
     * api/search/albumInfo/updateLatelyAlbumRanking
     * @return
     */
    @GetMapping("/albumInfo/updateLatelyAlbumRanking")
    public Result updateLatelyAlbumRanking();
}
