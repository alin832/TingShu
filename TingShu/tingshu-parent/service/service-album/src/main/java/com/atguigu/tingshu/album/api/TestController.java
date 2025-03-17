package com.atguigu.tingshu.album.api;

import com.atguigu.tingshu.album.service.TestService;
import com.atguigu.tingshu.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @date: 2024/6/21 10:48
 * @author: yz
 * @version: 1.0
 */
@RestController
@RequestMapping("api/album/test")
public class TestController {

    @Autowired
    private TestService testService;

    /**
     * 演示锁的操作
     * @return
     */
    @GetMapping("/testLock")
    public Result testLock (){

        testService.testLock();

        return Result.ok();
    }
}
