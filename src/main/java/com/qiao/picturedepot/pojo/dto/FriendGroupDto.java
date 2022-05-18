package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.Friendship;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendGroupDto {
    Long id;
    String name;
    List<Friendship> friendships;
}
