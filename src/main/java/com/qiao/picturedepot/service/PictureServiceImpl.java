package com.qiao.picturedepot.service;

import com.qiao.picturedepot.config.MyProperties;
import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.PictureMapper;
import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.pojo.Album;
import com.qiao.picturedepot.pojo.Picture;
import com.qiao.picturedepot.pojo.PictureGroup;
import com.qiao.picturedepot.pojo.User;
import com.qiao.picturedepot.util.FileUtil;
import com.qiao.picturedepot.util.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PictureServiceImpl implements PictureService{
    @Autowired
    PictureGroupMapper pictureGroupMapper;
    @Autowired
    PictureMapper pictureMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    AlbumMapper albumMapper;
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
    public void getPicture(BigInteger pictureGroupId, BigInteger pictureId, OutputStream outputStream) {
        String path = pictureMapper.getPicturePath(pictureGroupId, pictureId);

        if(path != null){
            String filepath = myProperties.getPictureDepotPath() + path;
            try(InputStream inputStream = new FileInputStream(filepath)){
                FileUtil.copy(inputStream, outputStream);
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
    }

    @Override
    public List<Picture> getPicturesOfGroup(BigInteger pictureGroupId) {
        return pictureMapper.getPicturesByGroup(pictureGroupId);
    }

    @Override
    public List<BigInteger> addPicturesToGroup(BigInteger pictureGroupId, MultipartFile[] multipartFiles) {
        if(multipartFiles == null) {
            return new ArrayList<>();
        }

        PictureGroup pictureGroup = pictureGroupMapper.getPictureGroupById(pictureGroupId);
        BigInteger albumId = pictureGroup.getAlbum();
        Album album = albumMapper.getAlbumById(albumId);
        User user = userMapper.getUserById(album.getOwner());
        String username = user.getUsername();
        long maxPictureSize = myProperties.getMaxPictureSize();

        //储存目录 ....../username/albumId/
        File dir = new File(myProperties.getPictureDepotPath() + username + File.separator + albumId);

        Integer sequence = pictureGroupMapper.getMaxPictureSequenceInGroup(pictureGroupId);
        if(sequence == null){
            sequence = 0;
        }

        List<BigInteger> pictureIds = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles) {
            String format = FileUtil.getNameSuffix(multipartFile.getOriginalFilename());
            if(multipartFile.getSize() > maxPictureSize || !FileUtil.isPicture(format)){
                //大小超限 或 格式错误 忽略
                continue;
            }

            try{
                //接收
                byte[] pictureData = multipartFile.getBytes();

                //生成文件名
                File file = null;
                do{
                    file = new File(dir, UUID.randomUUID() + "." + format);
                }while(file.exists());

                //保存
                if(!dir.exists()){
                    dir.mkdirs();
                }
                try(OutputStream outputStream = new FileOutputStream(file)){
                    FileUtil.save(pictureData, outputStream);
                }

                //记录图片信息
                sequence++;
                Picture picture = new Picture();
                picture.setPictureFormat(format);
                picture.setPictureGroupId(pictureGroupId);
                picture.setFilepath(file.getPath().substring(myProperties.getPictureDepotPath().length()));
                picture.setSequence(sequence);
                if(pictureMapper.addPicture(picture) == 1){
                    pictureIds.add(picture.getId());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return pictureIds;
    }

    @Override
    public void deletePictureGroup(BigInteger pictureGroupId) {
        List<Picture> pictures = getPicturesOfGroup(pictureGroupId);
        for (Picture picture : pictures) {
            String relativePath = pictureMapper.getPicturePath(pictureGroupId, picture.getId());
            if(relativePath != null){
                File pictureFile = new File(myProperties.getPictureDepotPath(), relativePath);
                System.out.println("删除"+pictureFile.getPath()+" "+pictureFile.delete());
            }

            pictureMapper.deletePicture(pictureGroupId, picture.getId());
        }

        pictureGroupMapper.deletePictureGroupById(pictureGroupId);
    }

    @Override
    public void updatePictureSequences(BigInteger pictureGroupId, List<BigInteger> idSequence) {
        for(int i = 0; i < idSequence.size(); i++){
            if(idSequence.get(i) != null){
                pictureMapper.updateSequence(pictureGroupId, idSequence.get(i), i);
            }
        }
    }

    @Override
    public void updatePictureGroup(PictureGroup pictureGroup) {
        pictureGroupMapper.updatePictureGroup(pictureGroup);
    }

    @Override
    public void deletePictures(BigInteger pictureGroupId, List<BigInteger> pictureIds) {
        if(pictureIds != null) {
            for (BigInteger pictureId : pictureIds) {
                String relativePath = pictureMapper.getPicturePath(pictureGroupId, pictureGroupId);
                File pictureFile = new File(myProperties.getPictureDepotPath(), relativePath);
                pictureFile.delete();

                pictureMapper.deletePicture(pictureGroupId, pictureId);
            }
        }
    }

    @Override
    public BigInteger addPictureGroup(PictureGroup pictureGroup) {
        pictureGroup.setId(null);
        pictureGroupMapper.addPictureGroup(pictureGroup);
        return pictureGroup.getId();
    }
}
