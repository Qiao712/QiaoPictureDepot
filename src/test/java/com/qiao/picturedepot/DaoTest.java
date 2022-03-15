package com.qiao.picturedepot;

import com.qiao.picturedepot.dao.FriendGroupMapper;
import com.qiao.picturedepot.dao.FriendMapper;
import com.qiao.picturedepot.pojo.domain.Friend;
import com.qiao.picturedepot.pojo.domain.FriendGroup;
import com.qiao.picturedepot.service.FriendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;
import java.util.List;

@SpringBootTest
public class DaoTest {
    @Autowired
    FriendMapper friendMapper;
    @Autowired
    FriendGroupMapper friendGroupMapper;
    @Autowired
    FriendService friendService;

    @Test
    void testFriendDao(){
//        List<Friend> friends = friendMapper.getFriendsByUserId(BigInteger.valueOf(1));
        List<Friend> friends = friendMapper.getFriendsByGroupId(BigInteger.valueOf(1));
        for (Friend friend : friends) {
            System.out.println(friend);
        }

//        friendMapper.addFriend(new Friend(null, BigInteger.valueOf(6), BigInteger.valueOf(1), null, null));
//        friendMapper.deleteFriendById(BigInteger.valueOf(4));
    }

    @Test
    void testFriendGroupDao(){
        List<FriendGroup> friendGroups = friendGroupMapper.getFriendGroupsByUserId(BigInteger.valueOf(1));
        for (FriendGroup friendGroup : friendGroups) {
            System.out.println(friendGroup);
        }
//        System.out.println(friendGroupMapper.getFriendGroupById(BigInteger.valueOf(1)));
//        friendGroupMapper.addFriendGroup(new FriendGroup(null, "水友", BigInteger.valueOf(1), null, null, null));
//        friendGroupMapper.deleteFriendGroupById(BigInteger.valueOf(3));
//        friendGroupMapper.updateFriendGroup(new FriendGroup(BigInteger.valueOf(1), "狐朋狗友", BigInteger.valueOf(1), null, null));
    }

    @Test
    void testAddFriend(){
        friendService.addFriend(BigInteger.valueOf(7), "死党",BigInteger.valueOf(1), "水友");
//        friendService.deleteFriend(BigInteger.valueOf(7), BigInteger.valueOf(555));
    }
}
