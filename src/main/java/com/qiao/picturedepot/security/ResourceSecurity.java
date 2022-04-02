package com.qiao.picturedepot.security;

import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.exception.AuthorizationException;
import com.qiao.picturedepot.pojo.domain.Album;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component("rs")
public class ResourceSecurity {
    @Autowired
    PictureGroupMapper pictureGroupMapper;
    @Autowired
    AlbumMapper albumMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 判断当前用户对相册的访问权
     */
    public boolean canAccessAlbum(Album album) {
        if (album == null) {
            return true;
        }

        //是否公开
        if(album.isPublic()){
            return true;
        }

        //访问者是否为属主
        User user = SecurityUtil.getCurrentUser();
        if(user != null){
            return user.getId().equals(album.getOwnerId());
        }else{
            return false;
        }
    }

    /**
     * 判断当前用户对相册的访问权
     */
    public boolean canAccessAlbum(BigInteger albumId){
        Album album = albumMapper.getAlbumById(albumId);
        return canAccessAlbum(album);
    }

    /**
    * 判断当前用户对图组的访问权
    */
    public boolean canAccessPictureGroup(BigInteger pictureGroupId){
        User user = SecurityUtil.getCurrentUser();
        if(user == null){
            return false;
        }

        if(pictureGroupId == null){
            return true;
        }

        //通过缓存的访问信息判断是否允许访问
        if(checkByCache(user.getUsername(), pictureGroupId)){
            return true;
        }

        PictureGroup pictureGroup = pictureGroupMapper.getPictureGroupById(pictureGroupId);
        Album album = null;
        if(pictureGroup != null){
            BigInteger albumId = pictureGroup.getAlbumId();
            if(albumId != null){
                album = albumMapper.getAlbumById(albumId);
            }
        }

        //Album不存在，禁止访问未知属主的图片
        if(album == null) {
            return false;
        }

        //Album为public，允许访问
        if(album.isPublic()){
            return true;
        }

        //判断所属
        if(Objects.equals(album.getOwnerId(), user.getId())){
            //缓存访问信息
            cache(user.getUsername(), pictureGroupId);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 通过缓存判断图组的访问权
     */
    private boolean checkByCache(String username, BigInteger pictureGroupId){
        String cachedPictureGroupId = null;

        try {
            cachedPictureGroupId = redisTemplate.opsForValue().get(username);
        }catch (Exception e){
            //TODO: 异常处理
            e.printStackTrace();
        }

        if(cachedPictureGroupId != null){
            return cachedPictureGroupId.equals(pictureGroupId.toString());
        }else{
            return false;
        }
    }


    /**
     * 缓存图组访问权 (pictureGroupId - userId)
     */
    private void cache(String username, BigInteger pictureGroupId){
        try {
            //pictureGroupId - userId 对 60秒后失效
            redisTemplate.opsForValue().set(username, pictureGroupId.toString(), 60, TimeUnit.SECONDS);
        }catch (Exception e){
            //TODO: 异常处理
            e.printStackTrace();
        }
    }
}
