package com.atguigu.cdc.model;

import lombok.Data;

import javax.persistence.Column;

/**
 * @date: 2024/6/22 15:34
 * @author: yz
 * @version: 1.0
 */
@Data
public class AlbumCdcEntity {
    @Column(name = "id")
    private Long id;

    @Column(name = "album_title")
    private String albumTitle;

}
