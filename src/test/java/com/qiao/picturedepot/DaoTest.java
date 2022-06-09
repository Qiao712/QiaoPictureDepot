package com.qiao.picturedepot;

import com.qiao.picturedepot.dao.*;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.service.FriendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;

import java.io.IOException;
import java.io.InputStream;

@SpringBootTest
public class DaoTest {
    @Autowired
    FriendshipMapper friendshipMapper;
    @Autowired
    FriendGroupMapper friendGroupMapper;
    @Autowired
    FriendService friendService;

    @Test
    void testFriendDao(){
//        List<Friend> friends = friendMapper.getFriendsByUserId(BigInteger.valueOf(1));
//        List<FriendShip> friendShips = friendMapper.listByGroupId(BigInteger.valueOf(1));
//        for (FriendShip friendShip : friendShips) {
//            System.out.println(friendShip);
//        }

//        friendMapper.addFriend(new Friend(null, BigInteger.valueOf(6), BigInteger.valueOf(1), null, null));
//        friendMapper.deleteFriendById(BigInteger.valueOf(4));
    }

    @Test
    void testFriendGroupDao(){
//        List<FriendGroup> friendGroups = friendGroupMapper.listByUserId(BigInteger.valueOf(1));
//        for (FriendGroup friendGroup : friendGroups) {
//            System.out.println(friendGroup);
//        }
//        System.out.println(friendGroupMapper.getFriendGroupById(BigInteger.valueOf(1)));
//        friendGroupMapper.addFriendGroup(new FriendGroup(null, "水友", BigInteger.valueOf(1), null, null, null));
//        friendGroupMapper.deleteFriendGroupById(BigInteger.valueOf(3));
//        friendGroupMapper.updateFriendGroup(new FriendGroup(BigInteger.valueOf(1), "狐朋狗友", BigInteger.valueOf(1), null, null));
    }

    @Test
    void testAddFriend(){
//        friendService.addFriend(BigInteger.valueOf(7), "死党",BigInteger.valueOf(1), "水友");
//        friendService.deleteFriend(BigInteger.valueOf(7), BigInteger.valueOf(555));
    }

    @Autowired
    private PictureGroupMapper pictureGroupMapper;
    @Test
    void testUpdate(){
        PictureGroup pictureGroup = new PictureGroup();
        pictureGroup.setDescription("xxxx");
        pictureGroup.setId(99L);
        pictureGroupMapper.update(pictureGroup);
    }

    @Autowired
    private UserMapper userMapper;
    @Test
    void testAvatar() throws IOException {
        InputStream inputStream = userMapper.getAvatarByUserId(1L);
        System.out.println(inputStream.available());
    }

    @Autowired
    private CommentMapper commentMapper;
    @Test
    void testCommentMapper(){
        System.out.println(commentMapper.increaseLikeCount(1L, 2L, 1));
        System.out.println(commentMapper.deleteCommentLikeDetail(1L, 2L));
        System.out.println(commentMapper.addCommentLikeDetail(1L, 2222L));
    }

    @Test
    void testResourceUser(){
        pictureGroupMapper.updateFileSize(99L);
    }
}
