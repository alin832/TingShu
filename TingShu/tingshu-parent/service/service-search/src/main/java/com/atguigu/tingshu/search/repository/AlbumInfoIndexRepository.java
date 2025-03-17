package com.atguigu.tingshu.search.repository;

import com.atguigu.tingshu.model.search.AlbumInfoIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @date: 2024/6/14 9:41
 * @author: yz
 * @version: 1.0
 */
public interface AlbumInfoIndexRepository  extends ElasticsearchRepository<AlbumInfoIndex,Long> {
}
