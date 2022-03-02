package com.qiao.picturedepot.security;

import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.pojo.User;
import com.qiao.picturedepot.service.AlbumService;
import com.qiao.picturedepot.service.PictureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

@Component
public class PictureAccessAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    @Autowired
    private PictureService pictureService;
    @Autowired
    private AlbumService albumService;

    //图片所属的picture group的所有者是否是当前用户
    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {
        Map<String, String> variables = requestAuthorizationContext.getVariables();

        BigInteger pictureGroupId = null;
        try{
            pictureGroupId = new BigInteger(variables.get("pictureGroupId"));
        }catch (NumberFormatException e){
            return new AuthorizationDecision(false);
        }

        PictureGroup pictureGroup = pictureService.getPictureGroupById(pictureGroupId);
        Album album = null;
        if(pictureGroup != null){
            BigInteger albumId = pictureGroup.getAlbum();
            if(albumId != null){
                album = albumService.getAlbumById(albumId);
            }
        }

        //判断所属
        Object user = authentication.get().getPrincipal();

        if(user instanceof User && Objects.equals(album.getOwner(), ((User)user).getId())){
            return new AuthorizationDecision(true);
        }else{
            return new AuthorizationDecision(false);
        }
    }
}
