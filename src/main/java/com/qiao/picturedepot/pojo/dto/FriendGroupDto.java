package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.Friendship;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendGroupDto {
    Long id;
    String name;
    List<Friendship> friendships;
}
