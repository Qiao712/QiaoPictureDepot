package com.qiao.picturedepot.pojo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Album extends BaseEntity{
    private String name;
    private String description;
    @JsonIgnore
    private boolean isPublic;
    private Long ownerId;
}
