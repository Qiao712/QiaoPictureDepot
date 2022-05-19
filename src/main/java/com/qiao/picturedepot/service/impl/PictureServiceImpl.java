package com.qiao.picturedepot.service.impl;

import com.qiao.picturedepot.config.MyProperties;
import com.qiao.picturedepot.dao.*;
import com.qiao.picturedepot.exception.ServiceException;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.domain.PictureIdentity;
import com.qiao.picturedepot.pojo.domain.PictureRef;
import com.qiao.picturedepot.pojo.dto.PictureGroupPreviewDto;
import com.qiao.picturedepot.pojo.dto.PictureGroupUpdateRequest;
import com.qiao.picturedepot.service.PictureService;
import com.qiao.picturedepot.service.PictureStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class PictureServiceImpl implements PictureService {
    @Autowired
    PictureGroupMapper pictureGroupMapper;
    @Autowired
    PictureRefMapper pictureRefMapper;
    @Autowired
    PictureIdentityMapper pictureIdentityMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    AlbumMapper albumMapper;
    @Autowired
    MyProperties myProperties;
    @Autowired
    PictureStoreService pictureStoreService;

    @Override
    @PreAuthorize("@rs.canAccessAlbum(#albumId)")
    public List<PictureGroupPreviewDto> getPictureGroupsByAlbum(Long albumId) {
        List<PictureGroup> pictureGroups = pictureGroupMapper.listByAlbumId(albumId);
        List<PictureGroupPreviewDto> pictureGroupPreviewDtos = new ArrayList<>(pictureGroups.size());

        for (PictureGroup pictureGroup : pictureGroups) {
            PictureGroupPreviewDto dto = new PictureGroupPreviewDto(pictureGroup);
            dto.setFirstPictureId( pictureRefMapper.getFirstPictureRefIdOfGroup(pictureGroup.getId()) );
            dto.setPictureCount( pictureRefMapper.countByGroupId(pictureGroup.getId()) );
            pictureGroupPreviewDtos.add(dto);
        }

        return pictureGroupPreviewDtos;
    }

    @Override
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public PictureGroup getPictureGroupById(Long pictureGroupId) {
        return pictureGroupMapper.getById(pictureGroupId);
    }

    @Override
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public PictureIdentity getPictureIdentity(Long pictureGroupId, Long pictureRefId) {
        Long pictureId = pictureRefMapper.getReferencedPictureId(pictureGroupId, pictureRefId);
        return pictureIdentityMapper.getById(pictureId);
    }

    @Override
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public List<PictureRef> getPictureListByGroup(Long pictureGroupId) {
        return pictureRefMapper.listByGroupId(pictureGroupId);
    }

    @Override
    @Transactional()
    @PreAuthorize("@rs.canAccessAlbum(#pictureGroup.albumId)")
    public void addPictureGroup(PictureGroup pictureGroup, MultipartFile[] picturesFiles) {
        //TODO: 参数检查错误处理
        if(pictureGroup.getAlbumId() == null) throw new ServiceException("图组所属的相册不可为空");

        pictureGroupMapper.add(pictureGroup);

        //TODO: 编程式事务控制
        int sequence = 0;
        List<PictureRef> pictureRefs = new ArrayList<>(picturesFiles.length);
        for (MultipartFile pictureFile : picturesFiles) {
            if(pictureFile == null) continue;

            //储存图片，引用计数增加
            PictureIdentity pictureIdentity = pictureStoreService.savePictureFile(pictureFile);

            PictureRef pictureRef = new PictureRef();
            pictureRef.setPictureGroupId(pictureGroup.getId());
            pictureRef.setPictureId(pictureIdentity.getId());
            pictureRef.setSequence(sequence++);
            pictureRefs.add(pictureRef);
        }
        pictureRefMapper.addBatch(pictureRefs);
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupUpdateRequest.pictureGroupId) and" +
                  "@rs.canAccessAlbum(#pictureGroupUpdateRequest.albumId)")
    public void updatePictureGroup(PictureGroupUpdateRequest pictureGroupUpdateRequest, MultipartFile[] pictureFiles) {
        //TODO: 编程式事务
        Long pictureGroupId = pictureGroupUpdateRequest.getPictureGroupId();

        //删除PictureRef
        List<Long> deletingPictureIds = new ArrayList<>(pictureGroupUpdateRequest.getPicturesToDelete().size());
        for (Long pictureRefId : pictureGroupUpdateRequest.getPicturesToDelete()) {
            Long pictureId = pictureRefMapper.getReferencedPictureId(pictureGroupId, pictureRefId);
            if(pictureId == null){
                throw new ServiceException("尝试删除不存在的图片");
            }
            deletingPictureIds.add(pictureId);
            pictureRefMapper.delete(pictureGroupId, pictureRefId);
        }

        //更新图片排序 与 新增图片
        List<Long> idSequences = pictureGroupUpdateRequest.getIdSequence();
        //指向pictures中元素
        int j = 0;
        //删除后，添加前，图组中图片数量，用于判断是否每一个图片的位置都被重新确定
        int pictureCount = pictureRefMapper.countByGroupId(pictureGroupId);
        List<PictureRef> newPictureRefs = new ArrayList<>(pictureFiles.length);
        for(int i = 0; i < idSequences.size(); i++){
            Long pictureId = idSequences.get(i);
            if(pictureId == null){
                //位置i的id为空，即位置i要插入新图片
                if(j == pictureFiles.length){
                    //空位数量大于新插入图片数量
                    throw new ServiceException("次序参数非法");
                }

                PictureIdentity pictureIdentity = pictureStoreService.savePictureFile(pictureFiles[j]);

                PictureRef pictureRef = new PictureRef();
                pictureRef.setPictureGroupId(pictureGroupId);
                pictureRef.setPictureId(pictureIdentity.getId());
                pictureRef.setSequence(i);
                newPictureRefs.add(pictureRef);
            }else{
                //位置i的id不为空，id为idSequences[i]的图片次序为i
                pictureRefMapper.updateSequence(pictureGroupId, idSequences.get(i), i);
                pictureCount--;
            }
        }

        //判断是否原有图片都已经重新确定顺序
        if(pictureCount != 0){
            throw new ServiceException("次序参数非法");
        }

        //插入
        pictureRefMapper.addBatch(newPictureRefs);

        //更新图组属性
        PictureGroup pictureGroup = new PictureGroup();
        pictureGroup.setId(pictureGroupId);
        pictureGroup.setTitle(pictureGroupUpdateRequest.getTitle());
        pictureGroup.setAlbumId(pictureGroupUpdateRequest.getAlbumId());
        if(pictureGroup.getTitle() != null || pictureGroup.getAlbumId() != null) {
            pictureGroupMapper.update(pictureGroup);
        }

        //删除图片
        for (Long pictureId : deletingPictureIds) {
            pictureStoreService.releasePicture(pictureId);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public void deletePictureGroup(Long pictureGroupId) {
        List<PictureRef> pictureRefs = getPictureListByGroup(pictureGroupId);
        List<Long> deletingPictureIds = new ArrayList<>(pictureRefs.size());
        for (PictureRef pictureRef : pictureRefs) {
            deletingPictureIds.add(pictureRef.getPictureId());
        }

        pictureRefMapper.deleteByGroupId(pictureGroupId);
        pictureGroupMapper.deleteById(pictureGroupId);

        //删除图片
        for (Long pictureId: deletingPictureIds) {
            pictureStoreService.releasePicture(pictureId);
        }
    }
}
