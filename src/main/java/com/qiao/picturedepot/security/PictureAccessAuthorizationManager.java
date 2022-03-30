package com.qiao.picturedepot.security;

import com.qiao.picturedepot.pojo.domain.Album;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.service.AlbumService;
import com.qiao.picturedepot.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class PictureAccessAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    @Autowired
    private PictureService pictureService;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    //图片所属的picture group的所有者是否是当前用户
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {
        Map<String, String> variables = requestAuthorizationContext.getVariables();
        String pictureGroupIdStr = variables.get("pictureGroupId");

        //获取User
        Object userObj = authentication.get().getPrincipal();
        User user = null;
        if(userObj instanceof User){
            user = (User) userObj;
        }

        //通过缓存的访问信息判断是否允许访问
        if(user != null && checkByCache(user.getUsername(), pictureGroupIdStr)){
            return new AuthorizationDecision(true);
        }

        BigInteger pictureGroupId = null;
        try{
            pictureGroupId = new BigInteger(pictureGroupIdStr);
        }catch (NumberFormatException e){
            return new AuthorizationDecision(false);
        }

        PictureGroup pictureGroup = pictureService.getPictureGroupById(pictureGroupId);
        Album album = null;
        if(pictureGroup != null){
            BigInteger albumId = pictureGroup.getAlbumId();
            if(albumId != null){
                album = albumService.getAlbumById(albumId);
            }
        }

        //Album不存在，禁止访问未知属主的图片
        if(album == null) {
            return new AuthorizationDecision(false);
        }

        //Album为public，允许访问
        if(album.isPublic()){
            return new AuthorizationDecision(true);
        }

        //判断所属
        if(user != null && Objects.equals(album.getOwnerId(), ((User)user).getId())){
            //缓存访问信息
            cache(user.getUsername(), pictureGroupIdStr);

            return new AuthorizationDecision(true);
        }else{
            return new AuthorizationDecision(false);
        }
    }

    //通过缓存的信息判断访问权
    private boolean checkByCache(String username, String pictureGroupId){
        String cachedPictureGroupId = null;

        try {
            cachedPictureGroupId = redisTemplate.opsForValue().get(username);
        }catch (Exception e){
            //TODO: 异常处理
            e.printStackTrace();
        }

        if(cachedPictureGroupId != null){
            return cachedPictureGroupId.equals(pictureGroupId);
        }else{
            return false;
        }
    }

    //缓存访问信息 pictureGroupId - userId
    private void cache(String username, String pictureGroupId){
        try {
            //pictureGroupId - userId 对 60秒后失效
            redisTemplate.opsForValue().set(username, pictureGroupId.toString(), 60, TimeUnit.SECONDS);
        }catch (Exception e){
            //TODO: 异常处理
            e.printStackTrace();
        }
    }
}
