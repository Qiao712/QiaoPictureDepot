package com.qiao.picturedepot.dao.pictureRef;

import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;

public interface PictureRepository {
    /**
     * 保存图片文件返回标识
     * @param pictureFile 图片文件输入流
     * @return 返回图片文件路径
     * @Throws ServiceException 储存失败抛出ServiceException
     */
    String savePictureFile(MultipartFile pictureFile);

    boolean deletePictureFile(String pictureFileId);

    void getPictureFile(String pictureFileId, OutputStream outputStream);
}
