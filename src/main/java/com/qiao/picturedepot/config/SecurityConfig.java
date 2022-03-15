package com.qiao.picturedepot.config;

import com.qiao.picturedepot.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

@EnableWebSecurity
@EnableMethodSecurity           //开启函数安全
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService userServiceImpl;
    @Autowired
    private CustomizedAuthenticationEntryPoint customizedAuthenticationEntryPoint;
    @Autowired
    private AuthenticationSuccessHandlerImpl authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandlerImpl authenticationFailureHandler;
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;
    @Autowired
    private PictureAccessAuthorizationManager pictureAccessAuthorizationManager;
    @Autowired
    private AlbumAccessAuthorizationManager albumAccessAuthorizationManager;

    //配置PasswordEncoder
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .mvcMatchers("/api/picture-groups/{pictureGroupId}/**").access(pictureAccessAuthorizationManager)
                .mvcMatchers("/api/albums/{albumId}/**").access(albumAccessAuthorizationManager)
                .mvcMatchers("/api/register").permitAll()
                .anyRequest().authenticated()
        );

        http
            .formLogin()
            .loginProcessingUrl("/api/login")
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
        .and()
            .rememberMe()
            .userDetailsService(userServiceImpl)
            .tokenRepository(new InMemoryTokenRepositoryImpl())     //token储存策略
            .rememberMeParameter("remember-me")
            .tokenValiditySeconds(60*60*24*7)                       //token有效期7天
        .and()
            .logout()
            .logoutUrl("/api/logout")
            .logoutSuccessHandler(logoutSuccessHandler)
        .and()
            .sessionManagement()
            .maximumSessions(1);

        http.csrf().disable();  //关闭csrf

        //认证/授权异常处理
        http.exceptionHandling().authenticationEntryPoint(customizedAuthenticationEntryPoint);
    }
}
