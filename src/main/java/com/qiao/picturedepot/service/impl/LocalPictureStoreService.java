package com.qiao.picturedepot.service.impl;

import com.qiao.picturedepot.config.MyProperties;
import com.qiao.picturedepot.dao.PictureIdentityMapper;
import com.qiao.picturedepot.exception.BusinessException;
import com.qiao.picturedepot.pojo.domain.PictureIdentity;
import com.qiao.picturedepot.service.PictureStoreService;
import com.qiao.picturedepot.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

/**
 * 储存为本地文件
 */
@Component
public class LocalPictureStoreService implements PictureStoreService {
    @Autowired
    PictureIdentityMapper pictureIdentityMapper;
    @Autowired
    MyProperties properties;

    @Override
    @Transactional
    public PictureIdentity savePictureFile(MultipartFile pictureFile) {
        //TODO: 原子性
        PictureIdentity pictureIdentity = null;

        String filename = pictureFile.getOriginalFilename();
        String filetype = FileUtil.getNameSuffix(filename);
        if(FileUtil.isPictureFile(filetype)){
            //使用UUID加后缀名作为文件名
            filename = UUID.randomUUID() + "." + filetype;
            File file = new File(properties.getPictureDepotPath() + File.separator + filename);

            //保存图片并计算MD5
            try(FileOutputStream fileOutputStream = new FileOutputStream(file);) {
                byte[] picture = pictureFile.getBytes();
                byte[] md5 = FileUtil.md5Digest(picture);
                fileOutputStream.write(picture);

                pictureIdentity = new PictureIdentity();
                pictureIdentity.setUri(filename);
                pictureIdentity.setFormat(filetype);
                pictureIdentity.setRefCount(1L);
                pictureIdentity.setMd5(md5);
                pictureIdentityMapper.add(pictureIdentity);
            } catch (IOException e) {
                throw new RuntimeException("图片保存失败", e);
            }
        }else{
            throw new BusinessException("文件格式错误");
        }

        return pictureIdentity;
    }

    @Override
    @Transactional
    public void releasePicture(Long pictureId) {
        //必须保证对PictureIdentity行的独占！
        PictureIdentity pictureIdentity = pictureIdentityMapper.getById(pictureId);
        long refCount = pictureIdentity.getRefCount() - 1;
        pictureIdentity.setRefCount(refCount);

        if(refCount <= 0){
            //删除图片文件
            pictureIdentityMapper.delete(pictureId);
            getFile(pictureIdentity.getUri()).delete();
        }else{
            pictureIdentityMapper.updateRefCount(pictureId, refCount);
        }
    }

    @Override
    public void readPicture(String uri, OutputStream outputStream) {
        try(FileInputStream fileInputStream = new FileInputStream(getFile(uri))) {
            FileUtil.copy(fileInputStream, outputStream);
        } catch (IOException e) {
            throw new RuntimeException("无法读取图片文件", e);
        }
    }

    @Override
    public PictureIdentity readPicture(Long pictureId, OutputStream outputStream) {
        PictureIdentity pictureIdentity = pictureIdentityMapper.getById(pictureId);
        if(pictureIdentity == null){
            //TODO: 异常处理
            throw new RuntimeException("图片不存在");
        }

        readPicture(pictureIdentity.getUri(), outputStream);
        return pictureIdentity;
    }

    private File getFile(String uri){
        return new File(properties.getPictureDepotPath() + File.separator + uri);
    }

    //TODO: 相同图片合并

    //TODO: ***** 使用第三方OSS
}
