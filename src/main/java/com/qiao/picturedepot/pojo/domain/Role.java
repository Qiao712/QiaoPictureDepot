package com.qiao.picturedepot.pojo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {
    private String name;
    private Long albumNumLimit;
    private Long pictureStorageLimit;
}
