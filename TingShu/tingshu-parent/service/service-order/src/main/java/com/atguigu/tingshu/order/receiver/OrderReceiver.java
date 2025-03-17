package com.atguigu.tingshu.order.receiver;

import com.alibaba.nacos.common.utils.StringUtils;
import com.atguigu.tingshu.common.constant.KafkaConstant;
import com.atguigu.tingshu.order.service.OrderInfoService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @date: 2024/6/28 10:46
 * @author: yz
 * @version: 1.0
 */
@Component
@Slf4j
public class OrderReceiver {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 关闭订单消费
     *
     * 方法执行时机：当前bean装配完成后执行
     * 方法执行的次数：一次
     * PostConstruct
     */
    @PostConstruct
    public void orderCancal(){

        //获取阻塞队列
        RBlockingDeque<Object> blockingDeque = redissonClient.getBlockingDeque(KafkaConstant.QUEUE_ORDER_CANCEL);


        //创建单例线程池
        ExecutorService executorService = Executors.newSingleThreadExecutor();


        //执行提交任务
        executorService.submit(()->{
            while (true){

                String take = null;
                //获取消息
                try {
                    take = (String) blockingDeque.take();

                } catch (InterruptedException e) {
                    log.error("消费者异常，订单号：{}",take);
                    e.printStackTrace();
                }

                //判断关闭订单
                if(StringUtils.isNotEmpty(take)){

                    //调用关单业务
                    orderInfoService.orderCancal(take);
                    System.out.println("关闭订单");

                }


            }


        });




    }


}
