package com.qiao.picturedepot.util;

import com.qiao.picturedepot.exception.AuthorizationException;
import com.qiao.picturedepot.pojo.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全框架工具类
 */
public class SecurityUtil {
    /**
     * 获取当前用户
     * @return 若未登录（匿名）用户返回null
     */
    static public User getCurrentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof User){
            return (User) principal;
        }else{
            return null;
        }
    }

    /**
     * 获取当前用户
     * @return 若未登录(匿名用户)抛出AuthorizationException
     */
    static public User getNonAnonymousCurrentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof User){
            return (User) principal;
        }else{
            throw new AuthorizationException();
        }
    }
}
