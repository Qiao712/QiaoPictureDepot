package com.qiao.picturedepot.config;

import com.qiao.picturedepot.security.CustomizedAuthenticationEntryPoint;
import com.qiao.picturedepot.security.CustomizedAuthenticationFilter;
import com.qiao.picturedepot.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    CustomizedAuthenticationFilter customizedAuthenticationFilter;
    @Autowired
    CustomizedAuthenticationEntryPoint customizedAuthenticationEntryPoint;

    //暴露AuthenticationManager
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //配置PasswordEncoder
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //拦截忽略
//        web.ignoring().antMatchers("/public/**", "/test/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //放行登录接口
        http.authorizeRequests()
                .antMatchers("/api/login").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(customizedAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        //关闭csrf
        http.csrf().disable();

        //认证/授权异常处理
        http.exceptionHandling().authenticationEntryPoint(customizedAuthenticationEntryPoint);
    }
}
