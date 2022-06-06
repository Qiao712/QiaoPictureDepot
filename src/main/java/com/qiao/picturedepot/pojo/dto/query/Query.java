package com.qiao.picturedepot.pojo.dto.query;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Set;

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

    /**
     * 获取允许排序的字段
     * @return 允许排序的字段set，或null表示全部字段可进行排序
     */
    public Set<String> getFieldsAllowedToOrderBy(){
        return null;
    }

    /**
     * 检查orderBy所用字段是否合法
     */
    public final boolean checkOrderBy(){
        Set<String> fieldNamesAllowed = getFieldsAllowedToOrderBy();
        if(fieldNamesAllowed == null || orderBy == null){
            return true;
        }
        return fieldNamesAllowed.contains(orderBy);
    }
}
