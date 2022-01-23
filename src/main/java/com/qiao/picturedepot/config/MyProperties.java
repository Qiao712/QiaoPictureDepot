package com.qiao.picturedepot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class MyProperties {
    private String pictureDepotPath = "D:\\workplace\\Picture Depot Test\\";

    public MyProperties() {
    }

    public String getPictureDepotPath() {
        return pictureDepotPath;
    }

    public void setPictureDepotPath(String pictureDepotPath) {
        this.pictureDepotPath = pictureDepotPath;
    }
}
