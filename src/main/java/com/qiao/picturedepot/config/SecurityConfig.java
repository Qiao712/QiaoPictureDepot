package com.qiao.picturedepot.config;

import com.qiao.picturedepot.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserServiceImpl userService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        //拦截忽略
        web.ignoring().antMatchers("/public/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/","/register").permitAll()
                .anyRequest().authenticated();              //其他页面只有在认证后才可访问

        //设置登录页面
        http.formLogin()
                .loginPage("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/albums/1")
                .permitAll();
        http.rememberMe().rememberMeParameter("rememberMe");
        http.logout();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //从数据库查询用户是数据
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
