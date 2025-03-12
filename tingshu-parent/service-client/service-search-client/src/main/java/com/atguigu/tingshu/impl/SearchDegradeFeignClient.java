package com.atguigu.tingshu.impl;

import com.atguigu.tingshu.SearchFeignClient;
import com.atguigu.tingshu.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @date: 2024/6/29 16:14
 * @author: yz
 * @version: 1.0
 */
@Component
@Slf4j
public class SearchDegradeFeignClient implements SearchFeignClient {
    /**
     *  更新排行榜
     * @return
     */
    @Override
    public Result updateLatelyAlbumRanking() {

        log.info("【搜索微服务】调用方法updateLatelyAlbumRanking异常");

        return Result.fail();
    }
}
