package com.qiao.picturedepot.pojo.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 封装复杂查询
 */
@Data
public class Query {
    @Min(1)
    @NotNull
    private Integer pageNo;

    @Min(1)
    @NotNull
    private Integer pageSize;

    private String orderBy;

    private Boolean desc;   //是否为降序
}
