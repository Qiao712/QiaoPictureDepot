package com.qiao.picturedepot;

import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.PictureMapper;
import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.Picture;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.pojo.User;
import com.qiao.picturedepot.service.PictureService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.math.BigInteger;
import java.util.List;

@SpringBootTest
class PictureDepotApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	UserMapper userMapper;
	@Autowired
	AlbumMapper albumMapper;
	@Autowired
	PictureMapper pictureMapper;
	@Autowired
	PictureGroupMapper pictureGroupMapper;

	@Test
	void testUserDao(){
		User user = userMapper.getUserByUsername("admin");
		System.out.println(user);
//		userMapper.addUser("testUser1", "123456", "normal");
//		user = userMapper.getUserById(BigInteger.valueOf(1));
//		System.out.println(user);
	}

	@Test
	void tesetAlbumDao() {
//		BigInteger bigInteger = albumMapper.getAlbumCountByUsername("admin");
//		System.out.println(bigInteger);

//		List<Album> albums = albumMapper.getAlbumsByUsername("admin", BigInteger.valueOf(0), 10);
//		for (Album album : albums) {
//			System.out.println(album);
//		}
	}

	@Test
	void testPictureDao(){
//		List<Picture> pictures = pictureMapper.getPicturesByGroup(BigInteger.valueOf(1));
//		for (Picture picture : pictures) {
//			System.out.println(picture);
//		}

//		pictureMapper.updateSequence(BigInteger.valueOf(6), 101);
	}

	@Test
	void testPictureGroupDao(){
//		List<PictureGroup> pictureGroups = pictureGroupMapper.getPictureGroupsByAlbumId(BigInteger.valueOf(1), BigInteger.valueOf(1), 1000);
//		for (PictureGroup pictureGroup : pictureGroups) {
//			System.out.println(pictureGroup);
//		}

//		//无草稿则创建
//		PictureGroup pictureGroup;
//		pictureGroup = new PictureGroup();
//		pictureGroup.setTitle("untitled");
//		pictureGroup.setAlbum(BigInteger.valueOf(1));
//		pictureGroupMapper.addPictureGroup(pictureGroup);

		int sequence = pictureGroupMapper.getMaxPictureSequenceInGroup(BigInteger.valueOf(1));
		System.out.println(sequence);
	}

	@Autowired
	PictureService pictureService;

	@Test
	void testPictureService() throws IOException {
		InputStream inputStream = this.getClass().getResourceAsStream("/static/public/img/picture_lost.png");
		inputStream.read();
	}
}
