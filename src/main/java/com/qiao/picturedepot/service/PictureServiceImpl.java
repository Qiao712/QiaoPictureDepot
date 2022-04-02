package com.qiao.picturedepot.service;

import com.qiao.picturedepot.config.MyProperties;
import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.PictureMapper;
import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.pojo.domain.Album;
import com.qiao.picturedepot.pojo.domain.Picture;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.domain.User;
import com.qiao.picturedepot.pojo.dto.PictureGroupPreviewDto;
import com.qiao.picturedepot.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @PreAuthorize("@rs.canAccessAlbum(#albumId)")
    public List<PictureGroupPreviewDto> getPictureGroupsByAlbum(BigInteger albumId) {
        List<PictureGroup> pictureGroups = pictureGroupMapper.getPictureGroupsByAlbumId(albumId);
        List<PictureGroupPreviewDto> pictureGroupPreviewDtos = new ArrayList<>(pictureGroups.size());

        for (PictureGroup pictureGroup : pictureGroups) {
            PictureGroupPreviewDto dto = new PictureGroupPreviewDto(pictureGroup);
            dto.setFirstPictureId( pictureMapper.getFirstPictureOfGroup(pictureGroup.getId()) );
            dto.setPictureCount( pictureMapper.getPictureCountOfGroup(pictureGroup.getId()) );
            pictureGroupPreviewDtos.add(dto);
        }

        return pictureGroupPreviewDtos;
    }

    @Override
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public PictureGroup getPictureGroupById(BigInteger pictureGroupId) {
        return pictureGroupMapper.getPictureGroupById(pictureGroupId);
    }

    @Override
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
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
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public List<Picture> getPicturesByGroup(BigInteger pictureGroupId) {
        return pictureMapper.getPicturesByGroup(pictureGroupId);
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public List<BigInteger> addPicturesToGroup(BigInteger pictureGroupId, MultipartFile[] multipartFiles) {
        if(multipartFiles == null) {
            return new ArrayList<>();
        }

        PictureGroup pictureGroup = pictureGroupMapper.getPictureGroupById(pictureGroupId);
        BigInteger albumId = pictureGroup.getAlbumId();
        Album album = albumMapper.getAlbumById(albumId);
        User user = userMapper.getUserById(album.getOwnerId());
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
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public void deletePictureGroup(BigInteger pictureGroupId) {
        List<File> pictureFilesToDelete = new ArrayList<>();
        List<Picture> pictures = getPicturesByGroup(pictureGroupId);
        for (Picture picture : pictures) {
            String relativePath = pictureMapper.getPicturePath(pictureGroupId, picture.getId());
            if(relativePath != null){
                File pictureFile = new File(myProperties.getPictureDepotPath(), relativePath);
                pictureFilesToDelete.add(pictureFile);
            }

            pictureMapper.deletePicture(pictureGroupId, picture.getId());
        }

        pictureGroupMapper.deletePictureGroupById(pictureGroupId);

        //数据库操作完成后，再统一删除文件，以免回滚造成图片丢失
        for (File pictureFile : pictureFilesToDelete) {
            try{
                pictureFile.delete();
            }catch (SecurityException e){
                //无法删除图片文件
                System.err.println("图片文件 " + pictureFile.getPath() + "删除失败");
            }
        }
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public void updatePictureSequences(BigInteger pictureGroupId, List<BigInteger> idSequence) {
        for(int i = 0; i < idSequence.size(); i++){
            if(idSequence.get(i) != null){
                pictureMapper.updateSequence(pictureGroupId, idSequence.get(i), i);
            }
        }
    }

    @Override
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroup.id)")
    public void updatePictureGroup(PictureGroup pictureGroup) {
        pictureGroupMapper.updatePictureGroup(pictureGroup);
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroup.id)")
    public void deletePictures(BigInteger pictureGroupId, List<BigInteger> pictureIds) {
        //TODO: 对残余目录的删除
        //收集需要删除的图片文件列表
        List<File> pictureFilesToDelete = new ArrayList<>();

        if(pictureIds != null) {
            for (BigInteger pictureId : pictureIds) {
                String relativePath = pictureMapper.getPicturePath(pictureGroupId, pictureGroupId);
                File pictureFile = new File(myProperties.getPictureDepotPath(), relativePath);
                pictureFilesToDelete.add(pictureFile);

                pictureMapper.deletePicture(pictureGroupId, pictureId);
            }
        }

        //数据库操作完成后，再统一删除文件，以免回滚造成图片丢失
        for (File pictureFile : pictureFilesToDelete) {
            try{
                pictureFile.delete();
            }catch (SecurityException e){
                //无法删除图片文件
                System.err.println("图片文件 " + pictureFile.getPath() + "删除失败");
            }
        }
    }

    @Override
    @PreAuthorize("@rs.canAccessAlbum(#pictureGroup.albumId)")
    public void addPictureGroup(PictureGroup pictureGroup) {
        pictureGroupMapper.addPictureGroup(pictureGroup);
    }
}
