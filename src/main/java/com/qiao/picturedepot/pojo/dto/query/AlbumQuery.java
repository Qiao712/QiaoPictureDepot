package com.qiao.picturedepot.pojo.dto.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlbumQuery extends Query {
    private String ownerUsername;

    private static Set<String> fieldsAllowedToOrderBy = new HashSet<>();
    static {
        fieldsAllowedToOrderBy.add("createTime");
        fieldsAllowedToOrderBy.add("updateTime");
        fieldsAllowedToOrderBy.add("name");
    }

    @Override
    public Set<String> getFieldsAllowedToOrderBy() {
        return fieldsAllowedToOrderBy;
    }
}
