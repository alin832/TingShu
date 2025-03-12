package com.atguigu.tingshu.search.receiver;

import com.alibaba.nacos.common.utils.StringUtils;
import com.atguigu.tingshu.common.constant.KafkaConstant;
import com.atguigu.tingshu.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @date: 2024/6/14 16:06
 * @author: yz
 * @version: 1.0
 */
@Component
@Slf4j
public class SearchReceiver {


    @Autowired
    private SearchService searchService;

    /**
     * 专辑上架监听
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.QUEUE_ALBUM_UPPER)
    public void albumImport(ConsumerRecord<String,String> record){

        //获取消息内容
        String value = record.value();
        //判断
        if(StringUtils.isNotEmpty(value)){

            //转换类型
            Long albumId = Long.valueOf(value);
            searchService.upperAlbum(albumId);

        }


    }

    /**
     * 专辑下架监听
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.QUEUE_ALBUM_LOWER)
    public void albumLower(ConsumerRecord<String,String> record){

        //获取消息内容
        String value = record.value();
        //判断
        if(StringUtils.isNotEmpty(value)){

            //转换类型
            Long albumId = Long.valueOf(value);
            searchService.lowerAlbum(albumId);

        }


    }


}
