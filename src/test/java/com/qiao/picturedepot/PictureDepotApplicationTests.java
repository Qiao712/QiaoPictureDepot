package com.qiao.picturedepot;

import com.qiao.picturedepot.config.MyProperties;
import com.qiao.picturedepot.dao.RoleMapper;
import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.pojo.Role;
import com.qiao.picturedepot.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigInteger;

@SpringBootTest
class PictureDepotApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	UserMapper userMapper;

	@Test
	void testDao(){
//		User user = userMapper.getUserByUsername("admin");
//		System.out.println(user);

//		userMapper.addUser("testUser1", "123456", "normal");
	}

	@Autowired
	MyProperties myProperties;

	@Test
	void test(){
		System.out.println(myProperties.getPictureDepotPath());
	}
}
