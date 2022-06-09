package com.qiao.picturedepot.service.impl;

import com.github.pagehelper.PageInfo;
import com.qiao.picturedepot.config.Properties;
import com.qiao.picturedepot.dao.*;
import com.qiao.picturedepot.exception.BusinessException;
import com.qiao.picturedepot.pojo.domain.PictureGroup;
import com.qiao.picturedepot.pojo.domain.PictureIdentity;
import com.qiao.picturedepot.pojo.domain.PictureRef;
import com.qiao.picturedepot.pojo.dto.AuthUserDto;
import com.qiao.picturedepot.pojo.dto.PictureGroupDto;
import com.qiao.picturedepot.pojo.dto.ResourceUsageDto;
import com.qiao.picturedepot.pojo.dto.query.PictureGroupQuery;
import com.qiao.picturedepot.pojo.dto.PictureGroupUpdateRequest;
import com.qiao.picturedepot.service.PictureService;
import com.qiao.picturedepot.service.PictureStoreService;
import com.qiao.picturedepot.util.ObjectUtil;
import com.qiao.picturedepot.util.QueryUtil;
import com.qiao.picturedepot.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PictureServiceImpl implements PictureService {
    @Autowired
    private PictureGroupMapper pictureGroupMapper;
    @Autowired
    private PictureRefMapper pictureRefMapper;
    @Autowired
    private PictureIdentityMapper pictureIdentityMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AlbumMapper albumMapper;
    @Autowired
    private Properties properties;
    @Autowired
    private PictureStoreService pictureStoreService;

    @Override
    @PreAuthorize("@rs.canAccessAlbum(#albumId)")
    public PageInfo<PictureGroupDto> getPictureGroups(PictureGroupQuery pictureGroupQuery) {
        QueryUtil.startPage(pictureGroupQuery);
        List<PictureGroup> pictureGroups = pictureGroupMapper.listByAlbumId(pictureGroupQuery.getAlbumId());
        List<PictureGroupDto> pictureGroupDtos = pictureGroups.stream().map(this::pictureGroupDtoMap).collect(Collectors.toList());

        return new PageInfo<>(pictureGroupDtos);
    }

    @Override
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public PictureGroupDto getPictureGroupById(Long pictureGroupId) {
        return pictureGroupDtoMap(pictureGroupMapper.getById(pictureGroupId));
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
        if(pictureGroup.getAlbumId() == null) throw new BusinessException("图组所属的相册不可为空");
        if(picturesFiles == null || picturesFiles.length == 0) throw new BusinessException("不允许新建空相册");

        //TODO: 编程式事务控制
        //保存图片
        long fileSize = 0;  //文件总大小
        int sequence = 0;
        List<PictureRef> pictureRefs = new ArrayList<>(picturesFiles.length);
        for (MultipartFile pictureFile : picturesFiles) {
            if(pictureFile == null) continue;

            //储存图片，引用计数增加
            PictureIdentity pictureIdentity = pictureStoreService.savePictureFile(pictureFile);

            fileSize += pictureIdentity.getFileSize();

            PictureRef pictureRef = new PictureRef();
            pictureRef.setPictureId(pictureIdentity.getId());
            pictureRef.setSequence(sequence++);
            pictureRefs.add(pictureRef);
        }

        //添加图组
        pictureGroup.setFileSize(fileSize);
        pictureGroupMapper.add(pictureGroup);
        for (PictureRef pictureRef : pictureRefs) {
            pictureRef.setPictureGroupId(pictureGroup.getId());
        }
        if(! pictureRefs.isEmpty()){
            pictureRefMapper.addBatch(pictureRefs);
        }

        //更新统计信息------------------------------------------------------------------------------------------------
        //更新相册文件总大小信息
        albumMapper.updateFileSize(pictureGroup.getAlbumId(), fileSize);
        //更新用户的资源占用信息
        ResourceUsageDto resourceUsageIncrease = new ResourceUsageDto();
        resourceUsageIncrease.setPictureCount(pictureRefs.size());
        resourceUsageIncrease.setPictureGroupCount(1);
        resourceUsageIncrease.setSpaceUsage(fileSize);
        userMapper.updateResourceUsage(SecurityUtil.getNonAnonymousCurrentUser().getId(), resourceUsageIncrease);
        //---------------------------------------------------------------------------------------------------------
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupUpdateRequest.pictureGroupId) and" +
                  "@rs.canAccessAlbum(#pictureGroupUpdateRequest.albumId)")
    public void updatePictureGroup(PictureGroupUpdateRequest pictureGroupUpdateRequest, MultipartFile[] pictureFiles) {
        //TODO: 编程式事务
        Long pictureGroupId = pictureGroupUpdateRequest.getPictureGroupId();
        PictureGroup oldPictureGroup = pictureGroupMapper.getById(pictureGroupId);
        if(oldPictureGroup == null){
            throw new BusinessException("图组不存在");
        }

        //将null，替换为空集合
        if(pictureFiles == null){
            pictureFiles = new MultipartFile[0];
        }
        if(pictureGroupUpdateRequest.getPicturesToDelete() == null){
            pictureGroupUpdateRequest.setPicturesToDelete(new ArrayList<>());
        }
        if(pictureGroupUpdateRequest.getIdSequence() == null){
            pictureGroupUpdateRequest.setIdSequence(new ArrayList<>());
        }

        //删除PictureRef
        List<Long> deletingPictureIds = new ArrayList<>(pictureGroupUpdateRequest.getPicturesToDelete().size());
        for (Long pictureRefId : pictureGroupUpdateRequest.getPicturesToDelete()) {
            Long pictureId = pictureRefMapper.getReferencedPictureId(pictureGroupId, pictureRefId);
            if(pictureId == null){
                throw new BusinessException("尝试删除不存在的图片");
            }
            deletingPictureIds.add(pictureId);
            pictureRefMapper.delete(pictureGroupId, pictureRefId);
        }

        //删除后，添加前，图组中图片数量
        int pictureCountAfterDelete = pictureRefMapper.countByGroupId(pictureGroupId);
        //更新图片排序 与 新增图片
        List<Long> idSequences = pictureGroupUpdateRequest.getIdSequence();
        //指向pictures中元素
        int j = 0;
        //对原有图片重排的次数
        int k = 0;

        List<PictureRef> newPictureRefs = new ArrayList<>(pictureFiles.length);
        for(int i = 0; i < idSequences.size(); i++){
            Long pictureRefId = idSequences.get(i);
            if(pictureRefId == null){
                //位置i的id为空，即位置i要插入新图片
                if(j == pictureFiles.length){
                    //空位数量大于新插入图片数量
                    throw new BusinessException("次序参数非法");
                }
                PictureIdentity pictureIdentity = pictureStoreService.savePictureFile(pictureFiles[j++]);

                PictureRef pictureRef = new PictureRef();
                pictureRef.setPictureGroupId(pictureGroupId);
                pictureRef.setPictureId(pictureIdentity.getId());
                pictureRef.setSequence(i);
                newPictureRefs.add(pictureRef);
            }else{
                //位置i的id不为空，id为idSequences[i]的图片次序为i
                if(pictureRefMapper.updateSequence(pictureGroupId, pictureRefId, i) > 0) {
                    k++;
                }else{
                    throw new BusinessException("图片(pictureRefId = " + pictureRefId + ")不存在");
                }
            }
        }

        //确保未被删除的原有图片位置被重新确定，新图片刚好插入
        if(k != pictureCountAfterDelete || j != pictureFiles.length){
            throw new BusinessException("次序参数非法");
        }

        //插入
        if(!newPictureRefs.isEmpty()){
            pictureRefMapper.addBatch(newPictureRefs);
        }

        //更新图组属性
        PictureGroup pictureGroup = new PictureGroup();
        ObjectUtil.copyBean(pictureGroupUpdateRequest, pictureGroup);
        pictureGroup.setId(pictureGroupId);
        if(pictureGroup.getAlbumId() != null || pictureGroup.getTitle() != null || pictureGroup.getDescription() != null)   //至少更新一个字段
            pictureGroupMapper.update(pictureGroup);
        pictureGroupMapper.updateFileSize(pictureGroupId);  //重新统计图组文件总大小

        //更新统计信息------------------------------------------------------------------------------------------------
        //更新相册文件总大小信息
        long fileSize = pictureGroupMapper.getFileSize(pictureGroupId);
        long fileSizeIncrease = fileSize - oldPictureGroup.getFileSize();
        albumMapper.updateFileSize(oldPictureGroup.getAlbumId(), fileSizeIncrease);
        //更新用户的资源占用信息
        int pictureCountIncrease = pictureFiles.length - pictureGroupUpdateRequest.getPicturesToDelete().size();
        ResourceUsageDto resourceUsageIncrease = new ResourceUsageDto();
        resourceUsageIncrease.setPictureCount(pictureCountIncrease);
        resourceUsageIncrease.setPictureGroupCount(0);
        resourceUsageIncrease.setSpaceUsage(fileSizeIncrease);
        userMapper.updateResourceUsage(SecurityUtil.getNonAnonymousCurrentUser().getId(), resourceUsageIncrease);
        //---------------------------------------------------------------------------------------------------------

        //删除图片
        for (Long pictureId : deletingPictureIds) {
            pictureStoreService.releasePicture(pictureId);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public void deletePictureGroup(Long pictureGroupId) {
        PictureGroup pictureGroup = pictureGroupMapper.getById(pictureGroupId);
        if(pictureGroup == null){
            throw new BusinessException("图组不存在");
        }

        List<PictureRef> pictureRefs = getPictureListByGroup(pictureGroupId);
        List<Long> deletingPictureIds = new ArrayList<>(pictureRefs.size());
        for (PictureRef pictureRef : pictureRefs) {
            deletingPictureIds.add(pictureRef.getPictureId());
        }

        //更新统计信息------------------------------------------------------------------------------------------------
        //更新相册文件总大小信息
        albumMapper.updateFileSize(pictureGroup.getAlbumId(), - pictureGroup.getFileSize());
        //更新用户的资源占用信息
        ResourceUsageDto resourceUsageIncrease = new ResourceUsageDto();
        resourceUsageIncrease.setPictureCount( - pictureRefs.size());
        resourceUsageIncrease.setPictureGroupCount(-1);
        resourceUsageIncrease.setSpaceUsage( - pictureGroup.getFileSize());
        userMapper.updateResourceUsage(SecurityUtil.getNonAnonymousCurrentUser().getId(), resourceUsageIncrease);
        //---------------------------------------------------------------------------------------------------------

        //删除对象
        pictureRefMapper.deleteByGroupId(pictureGroupId);
        pictureGroupMapper.deleteById(pictureGroupId);

        //删除图片
        for (Long pictureId: deletingPictureIds) {
            pictureStoreService.releasePicture(pictureId);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public void likePictureGroup(Long pictureGroupId) {
        AuthUserDto user = SecurityUtil.getNonAnonymousCurrentUser();

        if(pictureGroupMapper.existsPictureGroupLikeDetail(pictureGroupId, user.getId())){
            throw new BusinessException("不可重复点赞");
        }

        if(! pictureGroupMapper.increaseLikeCount(pictureGroupId, 1)){
            throw new BusinessException("图组不存在");
        }

        pictureGroupMapper.addPictureGroupLikeDetail(pictureGroupId, user.getId());
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public void undoLikePictureGroup(Long pictureGroupId) {
        AuthUserDto user = SecurityUtil.getNonAnonymousCurrentUser();

        if(! pictureGroupMapper.deletePictureGroupLikeDetail(pictureGroupId, user.getId())){
            throw new BusinessException("无点赞记录");
        }

        pictureGroupMapper.increaseLikeCount(pictureGroupId, -1);
    }

    //TODO: ***缩略图

    private PictureGroupDto pictureGroupDtoMap(PictureGroup pictureGroup){
        Long currentUserId = SecurityUtil.getNonAnonymousCurrentUser().getId();
        PictureGroupDto pictureGroupDto = new PictureGroupDto();
        ObjectUtil.copyBean(pictureGroup, pictureGroupDto);

        pictureGroupDto.setFirstPictureRefId( pictureRefMapper.getFirstPictureRefIdOfGroup(pictureGroup.getId()) );
        pictureGroupDto.setPictureCount( pictureRefMapper.countByGroupId(pictureGroup.getId()) );
        pictureGroupDto.setLiked( pictureGroupMapper.existsPictureGroupLikeDetail(pictureGroup.getId(), currentUserId) );

        return pictureGroupDto;
    }
}
