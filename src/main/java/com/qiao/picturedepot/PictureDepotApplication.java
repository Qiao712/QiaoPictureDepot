package com.qiao.picturedepot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@EnableAsync
@SpringBootApplication
public class PictureDepotApplication {
	public static void main(String[] args) {
		SpringApplication.run(PictureDepotApplication.class, args);
	}
}
