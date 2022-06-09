package com.qiao.picturedepot.service;

import com.qiao.picturedepot.pojo.domain.PictureIdentity;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;

public interface PictureStoreService {
    PictureIdentity savePictureFile(MultipartFile pictureFile);

    PictureIdentity releasePicture(Long pictureId);

    PictureIdentity readPicture(Long pictureId, OutputStream outputStream);

    void readPicture(String uri, OutputStream outputStream);
}
