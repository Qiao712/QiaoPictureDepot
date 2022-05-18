package com.qiao.picturedepot.config;

import org.springframework.stereotype.Component;

@Component
public class MyProperties {
    private String pictureDepotPath = "D:\\workplace\\Picture Depot Test";
    private int maxPictureSize = 1024 * 1024 * 10;  //10Mib

    public MyProperties() {
    }

    public String getPictureDepotPath() {
        return pictureDepotPath;
    }

    public void setPictureDepotPath(String pictureDepotPath) {
        this.pictureDepotPath = pictureDepotPath;
    }

    public int getMaxPictureSize() {
        return maxPictureSize;
    }

    public void setMaxPictureSize(int maxPictureSize) {
        this.maxPictureSize = maxPictureSize;
    }
}
