package com.atguigu.tingshu.user.startagy.impl;

import com.atguigu.tingshu.model.user.UserPaidAlbum;
import com.atguigu.tingshu.user.mapper.UserPaidAlbumMapper;
import com.atguigu.tingshu.user.startagy.ItemTypeStrategy;
import com.atguigu.tingshu.vo.user.UserPaidRecordVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @date: 2024/6/26 15:54
 * @author: yz
 * @version: 1.0
 */
@Component("1001")
public class AlbumStrategy implements ItemTypeStrategy {

    @Autowired
    private UserPaidAlbumMapper userPaidAlbumMapper;

    @Override
    public void savePaidRecord(UserPaidRecordVo userPaidRecordVo) {

        System.err.println("专辑处理执行。。。。。");
        //新增专辑记录-user_paid_album

        //判断是否已经新增过记录
        QueryWrapper<UserPaidAlbum> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("order_no",userPaidRecordVo.getOrderNo());
        //执行查询
        Long count = userPaidAlbumMapper.selectCount(queryWrapper);
        if(count>0){
            return;
        }

        //新增专辑购买记录
        UserPaidAlbum userPaidAlbum=new UserPaidAlbum();
        userPaidAlbum.setOrderNo(userPaidRecordVo.getOrderNo());
        userPaidAlbum.setUserId(userPaidRecordVo.getUserId());
        userPaidAlbum.setAlbumId(userPaidRecordVo.getItemIdList().get(0));


        userPaidAlbumMapper.insert(userPaidAlbum);

    }
}
