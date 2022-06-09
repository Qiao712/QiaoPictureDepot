package com.qiao.picturedepot.util;

import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 重新统计用户的资源占用情况，并更新对应字段
 * 相册数量，图片数量，空间占用
 */
@Component
public class StatisticsUtil {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PictureGroupMapper pictureGroupMapper;
    @Autowired
    private AlbumMapper albumMapper;

    public void updateResourceUsage(Long userId){
        userMapper.countResourceUsage(userId);
    }
}
