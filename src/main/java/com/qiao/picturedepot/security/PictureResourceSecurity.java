package com.qiao.picturedepot.security;

import com.qiao.picturedepot.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

@Component
public class PictureResourceSecurity {
    @Autowired
    private AlbumService albumService;

//    public boolean canAccessAlbum(Authentication authentication, BigInteger albumId){
//        if(albumId == null){
//            return true;
//        }
//
//
//    }
}
