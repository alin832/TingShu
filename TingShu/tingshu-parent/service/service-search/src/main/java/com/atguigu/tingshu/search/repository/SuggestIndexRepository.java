package com.atguigu.tingshu.search.repository;

import com.atguigu.tingshu.model.search.SuggestIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @date: 2024/6/17 11:12
 * @author: yz
 * @version: 1.0
 */
public interface SuggestIndexRepository  extends ElasticsearchRepository<SuggestIndex,String> {
}
