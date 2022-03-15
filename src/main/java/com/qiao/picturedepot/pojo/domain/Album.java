package com.qiao.picturedepot.pojo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
public class Album extends BaseEntity{
    private String name;
    private BigInteger ownerId;
    @JsonIgnore
    private boolean isPublic;
    @JsonIgnore
    private String secretKey;
}
