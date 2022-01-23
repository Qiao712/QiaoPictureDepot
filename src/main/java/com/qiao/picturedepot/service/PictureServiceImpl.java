package com.qiao.picturedepot.service;

import com.qiao.picturedepot.config.MyProperties;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.PictureMapper;
import com.qiao.picturedepot.pojo.Picture;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.util.FileUtil;
import com.qiao.picturedepot.util.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.util.List;

@Service
public class PictureServiceImpl implements PictureService{
    @Autowired
    PictureGroupMapper pictureGroupMapper;
    @Autowired
    PictureMapper pictureMapper;
    @Autowired
    MyProperties myProperties;

    @Override
    public List<PictureGroup> getPictureGroupsOfAlbum(BigInteger albumId, BigInteger pageNo, int pageSize) {
        BigInteger start = PageHelper.getStart(pageNo, pageSize);
        BigInteger count = getPictureGroupCountOfAlbum(albumId);
        if(start.compareTo(count) > 0 || start.compareTo(BigInteger.valueOf(0)) < 0){
            //超出范围
            return null;
        }
        return pictureGroupMapper.getPictureGroupsByAlbumId(albumId, start, pageSize);
    }

    @Override
    public BigInteger getPictureGroupCountOfAlbum(BigInteger albumId) {
        return pictureGroupMapper.getPictureGroupCountByAlbumId(albumId);
    }

    @Override
    public PictureGroup getPictureGroupById(BigInteger id) {
        return pictureGroupMapper.getPictureGroupById(id);
    }

    @Override
    public boolean getPicture(BigInteger pictureId, OutputStream outputStream) {
        Picture picture = pictureMapper.getPictureById(pictureId);

        boolean flag = false;
        if(picture != null){
            String filepath = myProperties.getPictureDepotPath() + picture.getFilepath();
            try(InputStream inputStream = new FileInputStream(filepath)){
                FileUtil.copy(inputStream, outputStream);
                flag = true;
            } catch (FileNotFoundException e) {
                //返回“图片失效”
                try(InputStream inputStream = this.getClass().getResourceAsStream("/static/public/img/picture_lost.png")){
                    FileUtil.copy(inputStream, outputStream);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }

                System.out.println(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            //返回“图片不存在”
            try(InputStream inputStream = this.getClass().getResourceAsStream("/static/public/img/picture_not_exists.png")){
                FileUtil.copy(inputStream, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return flag;
    }
}
