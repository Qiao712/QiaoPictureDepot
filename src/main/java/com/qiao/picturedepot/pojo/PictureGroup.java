package com.qiao.picturedepot.pojo;

import java.math.BigInteger;
import java.util.Date;

public class PictureGroup {
    private BigInteger id;
    private BigInteger album;
    private String title;
    private Date createTime;
    private Date updateTime;

    private BigInteger firstPicture;
    private Integer pictureCount;

    public PictureGroup() {
    }

    public PictureGroup(BigInteger id, BigInteger album, String title, Date createTime, Date updateTime, BigInteger firstPicture, Integer pictureCount) {
        this.id = id;
        this.album = album;
        this.title = title;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.firstPicture = firstPicture;
        this.pictureCount = pictureCount;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public BigInteger getAlbum() {
        return album;
    }

    public void setAlbum(BigInteger album) {
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public BigInteger getFirstPicture() {
        return firstPicture;
    }

    public void setFirstPicture(BigInteger firstPicture) {
        this.firstPicture = firstPicture;
    }

    public Integer getPictureCount() {
        return pictureCount;
    }

    public void setPictureCount(Integer pictureCount) {
        this.pictureCount = pictureCount;
    }

    @Override
    public String toString() {
        return "PictureGroup{" +
                "id=" + id +
                ", album=" + album +
                ", title='" + title + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", firstPicture=" + firstPicture +
                ", pictureCount=" + pictureCount +
                '}';
    }
}
