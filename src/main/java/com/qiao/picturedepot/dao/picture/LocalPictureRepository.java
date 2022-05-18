package com.qiao.picturedepot.dao.picture;

import com.qiao.picturedepot.config.MyProperties;
import com.qiao.picturedepot.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

/**
 * 储存为本地文件
 */
@Component
public class LocalPictureRepository implements PictureRepository{
    @Autowired
    MyProperties properties;

    @Override
    public String savePictureFile(MultipartFile pictureFile) {
        String uri = null;

        String filename = pictureFile.getOriginalFilename();
        String filetype = FileUtil.getNameSuffix(filename);
        if(FileUtil.isPictureFile(filetype)){
            //使用UUID加后缀名作为文件名
            filename = UUID.randomUUID() + "." + filetype;
            uri = filename;
            File file = new File(properties.getPictureDepotPath() + File.separator + filename);

            //保存图片
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                FileUtil.copy(pictureFile.getInputStream(), fileOutputStream);
            } catch (IOException e) {
                throw new RuntimeException("图片保存失败", e);
            }
        }

        return uri;
    }

    @Override
    public boolean deletePictureFile(String uri) {
        if(uri == null) return false;
        return getFile(uri).delete();
    }

    @Override
    public void getPictureFile(String uri, OutputStream outputStream) {
        try {
            FileInputStream fileInputStream = new FileInputStream(getFile(uri));
            FileUtil.copy(fileInputStream, outputStream);
        } catch (IOException e) {
            throw new RuntimeException("无法读取图片文件", e);
        }
    }

    private File getFile(String pictureFileId){
        return new File(properties.getPictureDepotPath() + File.pathSeparator + pictureFileId);
    }
}
