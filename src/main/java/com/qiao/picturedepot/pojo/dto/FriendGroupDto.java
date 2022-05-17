package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.FriendShip;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendGroupDto {
    BigInteger id;
    String name;
    List<FriendShip> friendShips;
}
