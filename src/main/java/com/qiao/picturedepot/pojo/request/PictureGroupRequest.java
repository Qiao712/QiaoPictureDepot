package com.qiao.picturedepot.pojo.request;

import org.springframework.security.core.parameters.P;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PictureGroupRequest {
    private BigInteger pictureGroupId;
    private String title;
    private BigInteger albumId;
    private List<BigInteger> idSequence;
    private List<BigInteger> picturesToDelete;

    public PictureGroupRequest() {
    }

    public PictureGroupRequest(BigInteger pictureGroupId, String title, BigInteger albumId, List<BigInteger> idSequence, List<BigInteger> picturesToDelete) {
        this.pictureGroupId = pictureGroupId;
        this.title = title;
        this.albumId = albumId;
        this.idSequence = idSequence;
        this.picturesToDelete = picturesToDelete;
    }

    public BigInteger getPictureGroupId() {
        return pictureGroupId;
    }

    public void setPictureGroupId(BigInteger pictureGroupId) {
        this.pictureGroupId = pictureGroupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigInteger getAlbumId() {
        return albumId;
    }

    public void setAlbumId(BigInteger albumId) {
        this.albumId = albumId;
    }

    public List<BigInteger> getIdSequence() {
        return idSequence;
    }

    public void setIdSequence(List<BigInteger> idSequence) {
        this.idSequence = idSequence;
    }

    public List<BigInteger> getPicturesToDelete() {
        return picturesToDelete;
    }

    public void setPicturesToDelete(List<BigInteger> picturesToDelete) {
        this.picturesToDelete = picturesToDelete;
    }
}
