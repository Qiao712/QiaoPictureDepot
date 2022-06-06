package com.qiao.picturedepot.pojo.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class PictureGroupQuery extends Query {
    @NotNull
    private Long albumId;

    private static Set<String> fieldsAllowedToOrderBy = new HashSet<>();
    static {
        fieldsAllowedToOrderBy.add("createTime");
        fieldsAllowedToOrderBy.add("updateTime");
        fieldsAllowedToOrderBy.add("title");
    }

    @Override
    public Set<String> getFieldsAllowedToOrderBy() {
        return fieldsAllowedToOrderBy;
    }
}
