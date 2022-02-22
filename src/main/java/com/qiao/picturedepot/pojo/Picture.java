package com.qiao.picturedepot.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigInteger;
import java.util.Date;

public class Picture {
    private BigInteger id;
    @JsonIgnore
    private String filepath;
    @JsonIgnore
    private String pictureFormat;
    private BigInteger pictureGroupId;
    private int sequence;
    private Date createTime;
    private Date updateTime;

    public Picture() {
    }

    public Picture(BigInteger id, String filepath, String pictureFormat, BigInteger pictureGroupId, int sequence, Date createTime, Date updateTime) {
        this.id = id;
        this.filepath = filepath;
        this.pictureFormat = pictureFormat;
        this.pictureGroupId = pictureGroupId;
        this.sequence = sequence;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getPictureFormat() {
        return pictureFormat;
    }

    public void setPictureFormat(String pictureFormat) {
        this.pictureFormat = pictureFormat;
    }

    public BigInteger getPictureGroupId() {
        return pictureGroupId;
    }

    public void setPictureGroupId(BigInteger pictureGroupId) {
        this.pictureGroupId = pictureGroupId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
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
        return "Picture{" +
                "id=" + id +
                ", filepath='" + filepath + '\'' +
                ", pictureFormat='" + pictureFormat + '\'' +
                ", pictureGroupId=" + pictureGroupId +
                ", sequence=" + sequence +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}