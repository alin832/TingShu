package com.atguigu.tingshu.common.delay;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @date: 2024/6/28 10:34
 * @author: yz
 * @version: 1.0
 */
@Component
@Slf4j
public class DelayMsgService {


    @Autowired
    private RedissonClient redissonClient;

    /**
     * 发送延迟消息定义
     * @param queueName
     * @param msg
     * @param delay
     */
    public void sendDelayMessage(String queueName,String msg,Long delay){

        try {
            //获取阻塞对象
            RBlockingDeque<String> blockingDeque = redissonClient.getBlockingDeque(queueName);
            //初始化延时对象
            RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);

            //发送消息
//            delayedQueue.offer(msg,delay, TimeUnit.MINUTES);
            delayedQueue.offer(msg,delay, TimeUnit.SECONDS);
            log.info("延迟关单消息发送成功，消息是：{}",msg);
        } catch (Exception e) {
            log.error("延迟关单消息发送异常，消息是：{}",msg);
            throw new RuntimeException(e);
        }


    }


}
