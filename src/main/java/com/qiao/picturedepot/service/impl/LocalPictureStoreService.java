package com.qiao.picturedepot.service.impl;

import com.qiao.picturedepot.config.Properties;
import com.qiao.picturedepot.dao.PictureIdentityMapper;
import com.qiao.picturedepot.exception.BusinessException;
import com.qiao.picturedepot.pojo.domain.PictureIdentity;
import com.qiao.picturedepot.service.PictureStoreService;
import com.qiao.picturedepot.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.UUID;

/**
 * 储存为本地文件
 */
@Component
public class LocalPictureStoreService implements PictureStoreService {
    @Autowired
    private PictureIdentityMapper pictureIdentityMapper;
    @Autowired
    private Properties properties;

    private final Logger log = LoggerFactory.getLogger(getClass());

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
            throw new BusinessException("无法获取图片");
        }

        readPicture(pictureIdentity.getUri(), outputStream);
        return pictureIdentity;
    }

    private File getFile(String uri){
        return new File(properties.getPictureDepotPath() + File.separator + uri);
    }

    /**
     * 每个线程两个buffer，用于文件比较
     */
    private final static ThreadLocal<ByteBuffer> localBuffer1 = new ThreadLocal<>();
    private final static ThreadLocal<ByteBuffer> localBuffer2 = new ThreadLocal<>();

    public void clearPicture(Long pictureId){
        PictureIdentity pictureIdentity = pictureIdentityMapper.getById(pictureId);
        if(pictureIdentity == null){
            log.error("图片(picture id:" + pictureId + ")不存在");
            return;
        }

        //获取或创建2个可以将一张图片全部读入的缓冲区
        ByteBuffer byteBuffer1 = localBuffer1.get();
        if(byteBuffer1 == null){
            byteBuffer1 = ByteBuffer.allocateDirect(properties.getMaxPictureSize() + 1024);
            localBuffer1.set(byteBuffer1);
        }
        ByteBuffer byteBuffer2 = localBuffer2.get();
        if(byteBuffer2 == null){
            byteBuffer2 = ByteBuffer.allocateDirect(properties.getMaxPictureSize() + 1024);
            localBuffer2.set(byteBuffer2);
        }

        //读入当前图片
        if(! readPicture(pictureIdentity, byteBuffer1)) return;
        byte[] md5 = FileUtil.md5Digest(byteBuffer1.array());


        //MD5相同的图片
        List<PictureIdentity> pictureIdentities = pictureIdentityMapper.getByMD5(pictureIdentity.getMd5());
        for (PictureIdentity identity : pictureIdentities) {
            File file1 = getFile(identity.getUri());
        }
    }

    public boolean readPicture(PictureIdentity pictureIdentity, ByteBuffer byteBuffer){
        try(FileInputStream inputStream1 = new FileInputStream(getFile(pictureIdentity.getUri()))){
            try(FileChannel channel1 = inputStream1.getChannel()){
                channel1.read(byteBuffer);
                byteBuffer.flip();
                if(byteBuffer.limit() != channel1.size()){
                    log.error("无法图片" + pictureIdentity.getUri() + "大小(" + channel1.size() + " bytes)超出限制");
                    return false;
                }
            }
        } catch (IOException e) {
            log.error("无法读取图片文件", e);
        }

        return true;
    }

    //TODO: 相同图片合并

    //TODO: ***** 使用第三方OSS
}
