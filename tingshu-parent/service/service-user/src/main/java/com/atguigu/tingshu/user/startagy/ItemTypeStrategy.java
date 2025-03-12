package com.atguigu.tingshu.user.startagy;

import com.atguigu.tingshu.vo.user.UserPaidRecordVo;

/**
 *
 * 策略接口
 *
 * @author: yz
 * @version: 1.0
 */
public interface ItemTypeStrategy {


    /**
     * 处理用户购买记录
     *
     * @param userPaidRecordVo
     */
    public void savePaidRecord(UserPaidRecordVo userPaidRecordVo);
}
