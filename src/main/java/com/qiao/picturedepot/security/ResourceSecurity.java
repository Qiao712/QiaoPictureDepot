package com.qiao.picturedepot.security;

import com.qiao.picturedepot.dao.AlbumAccessMapper;
import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.pojo.domain.Album;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component("rs")
public class ResourceSecurity {
    @Autowired
    private PictureGroupMapper pictureGroupMapper;
    @Autowired
    private AlbumMapper albumMapper;
    @Autowired
    private AlbumAccessMapper albumAccessMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(ResourceSecurity.class);

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

        User user = SecurityUtil.getCurrentUser();
        if(user != null){
            //是否为属主 或 被授权
            return  Objects.equals(user.getId(), album.getOwnerId())
                    || albumAccessMapper.existsByUserIdAndAlbumId(user.getId(), album.getId());
        }

        return false;
    }

    /**
     * 判断当前用户对相册的访问权
     */
    public boolean canAccessAlbum(Long albumId){
        Album album = albumMapper.getById(albumId);
        return canAccessAlbum(album);
    }

    /**
    * 判断当前用户对图组的访问权
    */
    public boolean canAccessPictureGroup(Long pictureGroupId){
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

        PictureGroup pictureGroup = pictureGroupMapper.getById(pictureGroupId);
        Album album = null;
        if(pictureGroup != null){
            Long albumId = pictureGroup.getAlbumId();
            if(albumId != null){
                album = albumMapper.getById(albumId);
            }
        }

        //Album不存在，禁止访问未知属主的图片
        if(album == null) {
            return false;
        }

        //判断Album访问权
        if(canAccessAlbum(album)){
            //缓存访问信息
            cache(user.getUsername(), pictureGroupId);
            return true;
        }

        return false;
    }

    /**
     * 通过缓存判断图组的访问权
     */
    private boolean checkByCache(String username, Long pictureGroupId){
        String cachedPictureGroupId = null;

        try {
            cachedPictureGroupId = redisTemplate.opsForValue().get(username);
        }catch (Exception e){
            log.error("redis 异常", e);
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
    private void cache(String username, Long pictureGroupId){
        try {
            //pictureGroupId - userId 对 60秒后失效
            redisTemplate.opsForValue().set(username, pictureGroupId.toString(), 60, TimeUnit.SECONDS);
        }catch (Exception e){
            log.error("redis 异常", e);
        }
    }

    //TODO: 使用更优的缓存
}
