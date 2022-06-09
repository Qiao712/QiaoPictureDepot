package com.qiao.picturedepot.pojo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserActivityDto {
    private Date time;
    private ActivityType activityType;
    private Long relevantId;    //与该活动相关的实体的id

    public enum ActivityType {
        CREATE_ALBUM,
        CREATE_PICTURE_GROUP
    }
}
