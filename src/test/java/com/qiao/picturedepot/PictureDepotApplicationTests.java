package com.qiao.picturedepot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.PictureMapper;
import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.Picture;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.pojo.User;
import com.qiao.picturedepot.service.AlbumService;
import com.qiao.picturedepot.service.PictureService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

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
	@Autowired
	AlbumService albumService;

	@Test
	void testUserDao(){
		User user = userMapper.getUserByUsername("admin");
		System.out.println(user);
//		userMapper.addUser("testUser1", "123456", "normal");
//		user = userMapper.getUserById(BigInteger.valueOf(1));
//		System.out.println(user);
	}

	@Test
	void testAlbumDao() {
//		List<Album> albums = albumMapper.getAlbums("admin", false, BigInteger.valueOf(0), 1000);
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

	//坑 isPublic --> public
	@Test
	void testAlbumJson() throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		Album album = objectMapper.readValue("{\"id\":56,\"albumName\":\"4545643fssss\",\"public\":true}", Album.class);
		System.out.println(album);
	}
}
