package com.qiao.picturedepot.pojo.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity{
    private String name;
}
