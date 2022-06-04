package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.Friendship;
import lombok.*;

import java.util.List;

/**
 * 一个好友分组内的好友信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendGroupDto {
    Long id;
    String name;
    List<Friendship> friendships;
}
