package com.qiao.picturedepot.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigInteger;
import java.util.Date;

public class Album {
    private BigInteger id;
    private String albumName;
    private BigInteger owner;
    @JsonIgnore
    private boolean isPublic;
    @JsonIgnore
    private String secretKey;
    private Date createTime;
    private Date updateTime;

    public Album() {
    }

    public Album(BigInteger id, String albumName, BigInteger owner, boolean isPublic, String secretKey, Date createTime, Date updateTime) {
        this.id = id;
        this.albumName = albumName;
        this.owner = owner;
        this.isPublic = isPublic;
        this.secretKey = secretKey;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public BigInteger getOwner() {
        return owner;
    }

    public void setOwner(BigInteger owner) {
        this.owner = owner;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Album{" +
                "id=" + id +
                ", albumName='" + albumName + '\'' +
                ", owner=" + owner +
                ", isPublic=" + isPublic +
                ", secretKey='" + secretKey + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
