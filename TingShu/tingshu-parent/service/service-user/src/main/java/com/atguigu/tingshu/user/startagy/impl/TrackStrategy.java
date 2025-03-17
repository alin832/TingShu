package com.atguigu.tingshu.user.startagy.impl;

import com.atguigu.tingshu.album.AlbumFeignClient;
import com.atguigu.tingshu.model.album.TrackInfo;
import com.atguigu.tingshu.model.user.UserPaidTrack;
import com.atguigu.tingshu.user.mapper.UserPaidTrackMapper;
import com.atguigu.tingshu.user.startagy.ItemTypeStrategy;
import com.atguigu.tingshu.vo.user.UserPaidRecordVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @date: 2024/6/26 15:55
 * @author: yz
 * @version: 1.0
 */
@Component("1002")
public class TrackStrategy implements ItemTypeStrategy {

    @Autowired
    private UserPaidTrackMapper userPaidTrackMapper;
    @Autowired
    private AlbumFeignClient albumFeignClient;

    @Override
    public void savePaidRecord(UserPaidRecordVo userPaidRecordVo) {

        System.err.println("声音处理执行。。。。。");
//新增声音记录-user_paid_track

        QueryWrapper<UserPaidTrack> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_no",userPaidRecordVo.getOrderNo());
        //执行查询
        Long count = userPaidTrackMapper.selectCount(queryWrapper);
        //判断
        if(count>0){
            return;
        }

        //查询声音详情
        TrackInfo trackInfo = albumFeignClient.getTrackInfo(userPaidRecordVo.getItemIdList().get(0)).getData();
        //新增声音记录
        userPaidRecordVo.getItemIdList().forEach(trackId->{

            //新增声音购买记录
            UserPaidTrack userPaidTrack = new UserPaidTrack();
            userPaidTrack.setOrderNo(userPaidRecordVo.getOrderNo());
            userPaidTrack.setUserId(userPaidRecordVo.getUserId());
            userPaidTrack.setAlbumId(trackInfo.getAlbumId());
            userPaidTrack.setTrackId(trackId);

            userPaidTrackMapper.insert(userPaidTrack);

        });

    }
}
