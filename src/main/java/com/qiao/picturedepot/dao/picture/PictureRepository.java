package com.qiao.picturedepot.dao.picture;

import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;

public interface PictureRepository {
    /**
     * 保存图片文件返回定位符
     * @param pictureFile 图片文件输入流
     * @return 返回图片文件路径
     * @Throws ServiceException 储存失败抛出ServiceException
     */
    String savePictureFile(MultipartFile pictureFile);

    boolean deletePictureFile(String uri);

    void getPictureFile(String uri, OutputStream outputStream);
}
