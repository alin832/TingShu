package com.atguigu.tingshu.album.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.StringUtils;
import com.atguigu.tingshu.album.service.TrackInfoService;
import com.atguigu.tingshu.common.constant.KafkaConstant;
import com.atguigu.tingshu.common.execption.GuiguException;
import com.atguigu.tingshu.vo.album.TrackStatMqVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @date: 2024/6/19 14:24
 * @author: yz
 * @version: 1.0
 */
@Component
@Slf4j
public class AlbumReceiver {

    @Autowired
    private TrackInfoService trackInfoService;


    /**
     * 声音统计监听
     * @param record
     */
    @KafkaListener(topics = KafkaConstant.QUEUE_TRACK_STAT_UPDATE)
    public  void updateTrackStat(ConsumerRecord<String,String> record){

        try {
            //获取消息数据
            String value = record.value();
            //判断
//            if(StringUtils.isNotEmpty(value)){
                //转换类型
                TrackStatMqVo trackStatMqVo = JSON.parseObject(value, TrackStatMqVo.class);

                //调用业务处理统计
                trackInfoService.updateTrackStat(trackStatMqVo);

//            }

            log.info("[专辑服务]，监听到更新声音统计消息：{}", value);
        } catch (Exception e) {

            log.error("[专辑服务]异常，监听到更新声音统计消息：{}",e.getMessage());
            throw new GuiguException(400,"统计监听异常");
        }


    }



}
