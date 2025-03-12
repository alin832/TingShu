package com.atguigu.tingshu.dispatch.job;

import com.atguigu.tingshu.SearchFeignClient;
import com.atguigu.tingshu.user.client.UserFeignClient;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DispatchHandler {


    @Autowired
    private SearchFeignClient searchFeignClient;
    /**
     * 定时更新排行榜
     * 0 0 0/1 * * ?
     */
    @XxlJob("updateHotAlbumJob")
    public void updateHotAlbumJob() {

        searchFeignClient.updateLatelyAlbumRanking();
        System.out.println("排行榜更新。。。。");

        XxlJobHelper.log("排行榜更新。。。.");

    }



    @Autowired
    private UserFeignClient userFeignClient;
    /**
     * 定时更新vip过期状态
     *0 0 0 * * ?
     */
    @XxlJob("updateUserVipStatus")
    public void updateUserVipStatus() {

        userFeignClient.updateUserVipStatus();
        System.out.println("vip过期状态更新。。。。");

        XxlJobHelper.log("vip过期状态更新。。。");

    }


    @XxlJob("firstJobHandler")
    public void firstJobHandler() {
        log.info("xxl-job项目集成测试");
        System.out.println("测试任务执行。。。。。");
    }
}