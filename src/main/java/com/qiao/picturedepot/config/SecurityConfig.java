package com.qiao.picturedepot.config;

import com.qiao.picturedepot.security.AuthenticationFailureHandlerImpl;
import com.qiao.picturedepot.security.AuthenticationSuccessHandlerImpl;
import com.qiao.picturedepot.security.CustomizedAuthenticationEntryPoint;
import com.qiao.picturedepot.security.LogoutSuccessHandlerImpl;
import com.qiao.picturedepot.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private CustomizedAuthenticationEntryPoint customizedAuthenticationEntryPoint;
    @Autowired
    private AuthenticationSuccessHandlerImpl authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandlerImpl authenticationFailureHandler;
    @Autowired
    private LogoutSuccessHandlerImpl logoutSuccessHandler;

    //配置PasswordEncoder
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //拦截忽略
//        web.ignoring().antMatchers("/api/login", "/test-login");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().authenticated();

        http
            .formLogin()
            .loginProcessingUrl("/api/login")
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
        .and()
            .rememberMe()
            .rememberMeParameter("remember-me")
            .tokenValiditySeconds(60*60*24*7)       //有效期7天
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
