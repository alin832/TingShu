package com.atguigu.cdc.model;

import lombok.Data;

import javax.persistence.Column;

/**
 * @date: 2024/6/22 15:16
 * @author: yz
 * @version: 1.0
 */
@Data
public class CDCEntity {


    @Column(name = "id")
    private Long id ;
}
