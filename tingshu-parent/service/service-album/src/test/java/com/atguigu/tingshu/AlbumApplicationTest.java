package com.atguigu.tingshu;

import com.atguigu.tingshu.common.constant.RedisConstant;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @date: 2024/6/22 11:48
 * @author: yz
 * @version: 1.0
 */
@SpringBootTest
public class AlbumApplicationTest {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void bloomInit111(){



//        for (int i = 1; i < 1602; i++) {

            RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConstant.ALBUM_BLOOM_FILTER);
            bloomFilter.add(Long.valueOf(1602));
//            System.out.println("加入："+i);

//        }


    }
}
