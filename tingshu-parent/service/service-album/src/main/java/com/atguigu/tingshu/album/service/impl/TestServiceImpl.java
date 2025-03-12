package com.atguigu.tingshu.album.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import com.atguigu.tingshu.album.service.TestService;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @date: 2024/6/21 10:50
 * @author: yz
 * @version: 1.0
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     *
     * redisson实现分布式锁的演示
     *
     * 1.获取redissonclient对象
     * 2.获取锁
     * 3.加锁
     * 4.释放锁
     *
     *
     */

    @Override
    @SneakyThrows
    public  void testLock() {
        //获取
        RLock lock = redissonClient.getLock("lock");
        //加锁
        lock.lock();
        //参数一：表示锁的超时时间，参数二：时间单位
        //boolean flag = lock.tryLock(7, TimeUnit.SECONDS);
        //参数一：获取锁前最大等待时间，参数二：表示锁的超时时间，参数三：时间单位
        boolean flag = lock.tryLock(100, 7, TimeUnit.SECONDS);
        if(flag){
            //获取num
            String num = redisTemplate.opsForValue().get("num");
            //转换类型
            if(StringUtils.isEmpty(num)){
                return;
            }
            int number = Integer.parseInt(num);


            redisTemplate.opsForValue().set("num",String.valueOf(++number));

            lock.unlock();

        }else{

            Thread.sleep(100);
            testLock();

        }

    }

    /**
     * redis实现分布式锁
     * 思想：占坑思想
     * 命令：setnx
     * 解决的问题：缓存击穿
     * 实现的步骤：
     *   1.setnx设置key，获取锁
     *   2.判断
     *   3.获取到了执行业务，业务执行完，删除锁
     *   4.自旋
     *
     * 问题：业务异常，锁无法释放的问题
     *  方案：使用expire设置一个超时时间
     *
     * 问题：加锁和设置过期时间非原子性操作
     *  方案：从 Redis 2.6.12 版本开始， SET 命令的行为可以通过一系列参数来修改
     *      set  ex nx
     *    加锁和设置过期时间可以原子实现
     *
     * 问题：设置了锁的超时时间后，因为一些原因导致锁超时，造成锁误删，导致共享资源区域出现无锁情况
     *  方案： 生成uuid，在获取锁时，添加记号
     *
     * 问题：添加记号以后，如果使用if判断，同样会造成误删，原因是判断和释放锁非原子性操作
     *
     *  方案：lua脚本解决
     *
     *
     *
     */
//    @Override
//    public  void testLock() {
//
//        //获取锁
////        Boolean flag = redisTemplate.opsForValue().setIfAbsent("lock", "lock");
////        //设置超时时间
////        redisTemplate.expire("lock",7, TimeUnit.SECONDS);
//
//        //生成uuid
//        String uuid= IdUtil.fastUUID();
//        //设置
//        Boolean flag = redisTemplate.opsForValue().setIfAbsent("lock", uuid,7,TimeUnit.SECONDS);
//        //判断
//        if(flag){
//            //获取num
//            String num = redisTemplate.opsForValue().get("num");
//            //转换类型
//            if(StringUtils.isEmpty(num)){
//                return;
//            }
//            int number = Integer.parseInt(num);
//            redisTemplate.opsForValue().set("num",String.valueOf(++number));
//
//            //定义lua脚本
//            String script="if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
//                    "then\n" +
//                    "    return redis.call(\"del\",KEYS[1])\n" +
//                    "else\n" +
//                    "    return 0\n" +
//                    "end";
//
//            //创建一个脚本对象
//            DefaultRedisScript<Long> redisScript = new DefaultRedisScript();
//            //设置返回值类型
//            redisScript.setResultType(Long.class);
//            //设置脚本
//            redisScript.setScriptText(script);
//
//
//            //发送lua脚本给redis
//            redisTemplate.execute(redisScript, Arrays.asList("lock"),uuid);
//
//
//            //获取锁值，判断
////            if(uuid.equals(redisTemplate.opsForValue().get("lock"))){
////                //释放锁
////                redisTemplate.delete("lock");
////            }
//        }else{
//            try {
//                Thread.sleep(100);
//                testLock();
//            } catch (InterruptedException e) {
//                   e.printStackTrace();
//            }
//        }
//
//
//    }


//    /**
//     *  本地锁的局限性演示
//     */
//    @Override
//    public synchronized void testLock() {
//
//        //获取num
//        String num = redisTemplate.opsForValue().get("num");
//        //转换类型
//        if(StringUtils.isEmpty(num)){
//            return;
//        }
//        int number = Integer.parseInt(num);
//
//
//        redisTemplate.opsForValue().set("num",String.valueOf(++number));
//
//    }
}
