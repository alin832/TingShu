package com.atguigu.tingshu.user.startagy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @date: 2024/6/26 15:57
 * @author: yz
 * @version: 1.0
 */
@Component
public class StrategyFactory {

    @Autowired
    private Map<String,ItemTypeStrategy> strategyMap;


    public ItemTypeStrategy getStrategy(String itemType){
        ItemTypeStrategy itemTypeStrategy = strategyMap.get(itemType);

        System.out.println("获取的策略实现类是："+itemTypeStrategy);
        return itemTypeStrategy;
    }

}
