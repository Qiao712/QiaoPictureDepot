package com.qiao.picturedepot;

import com.qiao.picturedepot.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@SpringBootApplication
public class PictureDepotApplication {
	public static void main(String[] args) {
		SpringApplication.run(PictureDepotApplication.class, args);
	}
}
