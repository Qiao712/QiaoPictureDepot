package com.qiao.picturedepot.security;

import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.User;
import com.qiao.picturedepot.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Map;
import java.util.function.Supplier;

@Component
public class AlbumAccessAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
    @Autowired
    private AlbumService albumService;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext requestAuthorizationContext) {
        Map<String, String> variables = requestAuthorizationContext.getVariables();
        BigInteger albumId = null;
        try{
            albumId = new BigInteger(variables.get("albumId"));
        }catch (NumberFormatException e){
            return new AuthorizationDecision(false);
        }

        Album album = null;
        if(albumId != null){
            album = albumService.getAlbumById(albumId);
        }

        BigInteger userId = null;
        Object user = authentication.get().getPrincipal();
        if(user instanceof User){
            userId = ((User) user).getId();
        }

        BigInteger ownerId = null;
        if(album != null){
            ownerId = album.getOwner();
        }

        if(ownerId != null && userId != null && userId.equals(ownerId)){
            return new AuthorizationDecision(true);
        }else{
            return new AuthorizationDecision(false);
        }
    }
}
