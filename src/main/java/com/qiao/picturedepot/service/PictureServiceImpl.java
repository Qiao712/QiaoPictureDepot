package com.qiao.picturedepot.service;

import com.qiao.picturedepot.config.MyProperties;
import com.qiao.picturedepot.dao.AlbumMapper;
import com.qiao.picturedepot.dao.PictureGroupMapper;
import com.qiao.picturedepot.dao.PictureMapper;
import com.qiao.picturedepot.dao.UserMapper;
import com.qiao.picturedepot.dao.pictureRef.PictureRepository;
import com.qiao.picturedepot.exception.ServiceException;
import com.qiao.picturedepot.pojo.domain.PictureRef;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.dto.PictureGroupPreviewDto;
import com.qiao.picturedepot.pojo.dto.PictureGroupUpdateRequest;
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
    @Autowired
    PictureRepository pictureRepository;

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
    public void getPictureFile(BigInteger pictureGroupId, BigInteger pictureId, OutputStream outputStream) {
        String pictureFileId = pictureMapper.getPicturePath(pictureGroupId, pictureId);
        pictureRepository.getPictureFile(pictureFileId, outputStream);
    }

    @Override
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public List<PictureRef> getPictureListByGroup(BigInteger pictureGroupId) {
        return pictureMapper.getPicturesByGroup(pictureGroupId);
    }

    @Override
    @Transactional()
    @PreAuthorize("@rs.canAccessAlbum(#pictureGroup.albumId)")
    public void addPictureGroup(PictureGroup pictureGroup, MultipartFile[] picturesFiles) {
        //TODO: 参数检查错误处理
        if(pictureGroup.getAlbumId() == null) throw new ServiceException("图组所属的相册不可为空");

        pictureGroupMapper.addPictureGroup(pictureGroup);

        //TODO: 编程式事务控制
        int sequence = 0;
        for (MultipartFile pictureFile : picturesFiles) {
            if(pictureFile == null) continue;

            String pictureFileId = pictureRepository.savePictureFile(pictureFile);

            PictureRef pictureRef = new PictureRef();
            pictureRef.setPictureGroupId(pictureGroup.getId());
            pictureRef.setFilepath(pictureFileId);
            pictureRef.setPictureFormat(FileUtil.getNameSuffix(pictureFile.getOriginalFilename()));
            pictureRef.setSequence(sequence++);
            pictureMapper.addPicture(pictureRef);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupUpdateRequest.pictureGroupId) and" +
                  "@rs.canAccessAlbum(#pictureGroupUpdateRequest.albumId)")
    public void updatePictureGroup(PictureGroupUpdateRequest pictureGroupUpdateRequest, MultipartFile[] pictureFiles) {
        //TODO: 编程式事务
        BigInteger pictureGroupId = pictureGroupUpdateRequest.getPictureGroupId();

        //删除图片
        List<String> pictureFileToDelete = new ArrayList<>();   //事务成功后再删除图片文件
        for (BigInteger pictureId : pictureGroupUpdateRequest.getPicturesToDelete()) {
            String pictureFileId = pictureMapper.getPicturePath(pictureGroupId, pictureId);
            if(pictureFileId != null) pictureFileToDelete.add(pictureFileId);

            pictureMapper.deletePicture(pictureGroupId, pictureId);
        }

        //更新图片排序 与 新增图片
        List<BigInteger> idSequences = pictureGroupUpdateRequest.getIdSequence();
        //指向pictures中元素
        int j = 0;
        //删除后，添加前，图组中图片数量，用于判断是否每一个图片的位置都被重新确定
        int pictureCount = pictureMapper.getPictureCountOfGroup(pictureGroupId);
        for(int i = 0; i < idSequences.size(); i++){
            BigInteger pictureId = idSequences.get(i);
            if(pictureId == null){
                //位置i的id为空，即位置i要插入新图片
                if(j == pictureFiles.length){
                    //空位数量大于新插入图片数量
                    throw new ServiceException("次序参数非法");
                }
                String pictureFileId = pictureRepository.savePictureFile(pictureFiles[j]);

                PictureRef pictureRef = new PictureRef();
                pictureRef.setPictureGroupId(pictureGroupId);
                pictureRef.setFilepath(pictureFileId);
                pictureRef.setPictureFormat(FileUtil.getNameSuffix(pictureFiles[j].getOriginalFilename()));
                pictureRef.setSequence(i);
                pictureMapper.addPicture(pictureRef);
            }else{
                //位置i的id不为空，id为idSequences[i]的图片次序为i
                pictureMapper.updateSequence(pictureGroupId, idSequences.get(i), i);
                pictureCount--;
            }
        }

        //判断是否原有图片都已经重新确定顺序
        if(pictureCount != 0){
            throw new ServiceException("次序参数非法");
        }

        //更新图组属性
        PictureGroup pictureGroup = new PictureGroup();
        pictureGroup.setId(pictureGroupId);
        pictureGroup.setTitle(pictureGroupUpdateRequest.getTitle());
        pictureGroup.setAlbumId(pictureGroupUpdateRequest.getAlbumId());
        pictureGroupMapper.updatePictureGroup(pictureGroup);

        //删除图片文件
        for (String pictureFileId : pictureFileToDelete) {
            pictureRepository.deletePictureFile(pictureFileId);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public void deletePictureGroup(BigInteger pictureGroupId) {
        List<String> pictureFilesToDelete = new ArrayList<>();
        List<PictureRef> pictureRefs = getPictureListByGroup(pictureGroupId);
        for (PictureRef pictureRef : pictureRefs) {
            if(pictureRef.getFilepath() != null) pictureFilesToDelete.add(pictureRef.getFilepath());
        }
        pictureMapper.deletePicturesByGroup(pictureGroupId);
        pictureGroupMapper.deletePictureGroupById(pictureGroupId);

        //数据库操作完成后，再统一删除文件，以免回滚造成图片丢失
        for (String pictureFileId : pictureFilesToDelete) {
            pictureRepository.deletePictureFile(pictureFileId);
        }
    }
}
