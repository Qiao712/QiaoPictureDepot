package com.qiao.picturedepot.dao;

import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.domain.UserWithAvatar;
import com.qiao.picturedepot.pojo.dto.ResourceUsageDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.io.InputStream;

@Mapper
public interface UserMapper {
    User getByUsername(String username);

    User getById(Long id);

    Long getUserIdByUsername(String username);

    String getUsernameById(Long id);

    Integer add(@Param("user") User user, @Param("rolename") String rolename);

    byte[] getAvatarByUserId(Long userId);

    Integer setAvatarByUserId(@Param("userId") Long userId, @Param("image") byte[] image);

    /**
     * 更新用户资源使用情况
     * @param resourceUsageIncr 资源使用情况的增量
     */
    Integer updateResourceUsage(Long userId, ResourceUsageDto resourceUsageIncr);

    /**
     * 重新统计用户资源使用情况字段(album_count, picture_group_count, picture_count)，已恢复一致性
     */
    Integer countResourceUsage(Long userId);

    UserWithAvatar getUserWithAvatar(Long id);
}
