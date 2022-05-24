package com.qiao.picturedepot.pojo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlbumAccess extends BaseEntity {
    private Long albumId;
    private Long friendGroupId;
}
