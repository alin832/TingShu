package com.atguigu.tingshu;

import com.atguigu.tingshu.common.constant.RedisConstant;
import com.atguigu.tingshu.search.service.SearchService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @date: 2024/6/14 15:47
 * @author: yz
 * @version: 1.0
 */
@SpringBootTest
public class BatchImportTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private RedissonClient redissonClient;




    @Test
    public void importAlbum(){


        for (int i = 0; i < 1602; i++) {


            try {
                searchService.upperAlbum(i+1l);
                System.out.println("导入专辑ID"+(i+1));
            } catch (Exception e) {
                continue;

            }


        }


    }


}
