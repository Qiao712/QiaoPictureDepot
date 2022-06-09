package com.qiao.picturedepot.pojo.dto;

import lombok.Data;

/**
 * 用户资源使用情况
 */
@Data
public class ResourceUsageDto {
    private Long userId;
    private Integer albumCount;
    private Integer pictureGroupCount;
    private Integer pictureCount;
    private Long spaceUsage;
}
