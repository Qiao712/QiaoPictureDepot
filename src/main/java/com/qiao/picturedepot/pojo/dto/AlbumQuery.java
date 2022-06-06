package com.qiao.picturedepot.pojo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlbumQuery extends Query{
    private String ownerUsername;
}
