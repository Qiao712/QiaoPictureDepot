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
        //TODO: ????????????????????????
        if(pictureGroup.getAlbumId() == null) throw new BusinessException("?????????????????????????????????");
        if(picturesFiles == null || picturesFiles.length == 0) throw new BusinessException("????????????????????????");

        //TODO: ?????????????????????
        //????????????
        long fileSize = 0;  //???????????????
        int sequence = 0;
        List<PictureRef> pictureRefs = new ArrayList<>(picturesFiles.length);
        for (MultipartFile pictureFile : picturesFiles) {
            if(pictureFile == null) continue;

            //?????????????????????????????????
            PictureIdentity pictureIdentity = pictureStoreService.savePictureFile(pictureFile);

            fileSize += pictureIdentity.getFileSize();

            PictureRef pictureRef = new PictureRef();
            pictureRef.setPictureId(pictureIdentity.getId());
            pictureRef.setSequence(sequence++);
            pictureRefs.add(pictureRef);
        }

        //????????????
        pictureGroup.setFileSize(fileSize);
        pictureGroupMapper.add(pictureGroup);
        for (PictureRef pictureRef : pictureRefs) {
            pictureRef.setPictureGroupId(pictureGroup.getId());
        }
        if(! pictureRefs.isEmpty()){
            pictureRefMapper.addBatch(pictureRefs);
        }

        //??????????????????------------------------------------------------------------------------------------------------
        //?????????????????????????????????
        albumMapper.updateFileSize(pictureGroup.getAlbumId(), fileSize);
        //?????????????????????????????????
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
        //TODO: ???????????????
        Long pictureGroupId = pictureGroupUpdateRequest.getPictureGroupId();
        PictureGroup oldPictureGroup = pictureGroupMapper.getById(pictureGroupId);
        if(oldPictureGroup == null){
            throw new BusinessException("???????????????");
        }

        //???null?????????????????????
        if(pictureFiles == null){
            pictureFiles = new MultipartFile[0];
        }
        if(pictureGroupUpdateRequest.getPicturesToDelete() == null){
            pictureGroupUpdateRequest.setPicturesToDelete(new ArrayList<>());
        }
        if(pictureGroupUpdateRequest.getIdSequence() == null){
            pictureGroupUpdateRequest.setIdSequence(new ArrayList<>());
        }

        //??????PictureRef
        List<Long> deletingPictureIds = new ArrayList<>(pictureGroupUpdateRequest.getPicturesToDelete().size());
        for (Long pictureRefId : pictureGroupUpdateRequest.getPicturesToDelete()) {
            Long pictureId = pictureRefMapper.getReferencedPictureId(pictureGroupId, pictureRefId);
            if(pictureId == null){
                throw new BusinessException("??????????????????????????????");
            }
            deletingPictureIds.add(pictureId);
            pictureRefMapper.delete(pictureGroupId, pictureRefId);
        }

        //?????????????????????????????????????????????
        int pictureCountAfterDelete = pictureRefMapper.countByGroupId(pictureGroupId);
        //?????????????????? ??? ????????????
        List<Long> idSequences = pictureGroupUpdateRequest.getIdSequence();
        //??????pictures?????????
        int j = 0;
        //??????????????????????????????
        int k = 0;

        List<PictureRef> newPictureRefs = new ArrayList<>(pictureFiles.length);
        for(int i = 0; i < idSequences.size(); i++){
            Long pictureRefId = idSequences.get(i);
            if(pictureRefId == null){
                //??????i???id??????????????????i??????????????????
                if(j == pictureFiles.length){
                    //???????????????????????????????????????
                    throw new BusinessException("??????????????????");
                }
                PictureIdentity pictureIdentity = pictureStoreService.savePictureFile(pictureFiles[j++]);

                PictureRef pictureRef = new PictureRef();
                pictureRef.setPictureGroupId(pictureGroupId);
                pictureRef.setPictureId(pictureIdentity.getId());
                pictureRef.setSequence(i);
                newPictureRefs.add(pictureRef);
            }else{
                //??????i???id????????????id???idSequences[i]??????????????????i
                if(pictureRefMapper.updateSequence(pictureGroupId, pictureRefId, i) > 0) {
                    k++;
                }else{
                    throw new BusinessException("??????(pictureRefId = " + pictureRefId + ")?????????");
                }
            }
        }

        //??????????????????????????????????????????????????????????????????????????????
        if(k != pictureCountAfterDelete || j != pictureFiles.length){
            throw new BusinessException("??????????????????");
        }

        //??????
        if(!newPictureRefs.isEmpty()){
            pictureRefMapper.addBatch(newPictureRefs);
        }

        //??????????????????
        PictureGroup pictureGroup = new PictureGroup();
        ObjectUtil.copyBean(pictureGroupUpdateRequest, pictureGroup);
        pictureGroup.setId(pictureGroupId);
        if(pictureGroup.getAlbumId() != null || pictureGroup.getTitle() != null || pictureGroup.getDescription() != null)   //????????????????????????
            pictureGroupMapper.update(pictureGroup);
        pictureGroupMapper.updateFileSize(pictureGroupId);  //?????????????????????????????????

        //??????????????????------------------------------------------------------------------------------------------------
        //?????????????????????????????????
        long fileSize = pictureGroupMapper.getFileSize(pictureGroupId);
        long fileSizeIncrease = fileSize - oldPictureGroup.getFileSize();
        albumMapper.updateFileSize(oldPictureGroup.getAlbumId(), fileSizeIncrease);
        //?????????????????????????????????
        int pictureCountIncrease = pictureFiles.length - pictureGroupUpdateRequest.getPicturesToDelete().size();
        ResourceUsageDto resourceUsageIncrease = new ResourceUsageDto();
        resourceUsageIncrease.setPictureCount(pictureCountIncrease);
        resourceUsageIncrease.setPictureGroupCount(0);
        resourceUsageIncrease.setSpaceUsage(fileSizeIncrease);
        userMapper.updateResourceUsage(SecurityUtil.getNonAnonymousCurrentUser().getId(), resourceUsageIncrease);
        //---------------------------------------------------------------------------------------------------------

        //????????????
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
            throw new BusinessException("???????????????");
        }

        List<PictureRef> pictureRefs = getPictureListByGroup(pictureGroupId);
        List<Long> deletingPictureIds = new ArrayList<>(pictureRefs.size());
        for (PictureRef pictureRef : pictureRefs) {
            deletingPictureIds.add(pictureRef.getPictureId());
        }

        //??????????????????------------------------------------------------------------------------------------------------
        //?????????????????????????????????
        albumMapper.updateFileSize(pictureGroup.getAlbumId(), - pictureGroup.getFileSize());
        //?????????????????????????????????
        ResourceUsageDto resourceUsageIncrease = new ResourceUsageDto();
        resourceUsageIncrease.setPictureCount( - pictureRefs.size());
        resourceUsageIncrease.setPictureGroupCount(-1);
        resourceUsageIncrease.setSpaceUsage( - pictureGroup.getFileSize());
        userMapper.updateResourceUsage(SecurityUtil.getNonAnonymousCurrentUser().getId(), resourceUsageIncrease);
        //---------------------------------------------------------------------------------------------------------

        //????????????
        pictureRefMapper.deleteByGroupId(pictureGroupId);
        pictureGroupMapper.deleteById(pictureGroupId);

        //????????????
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
            throw new BusinessException("??????????????????");
        }

        if(! pictureGroupMapper.increaseLikeCount(pictureGroupId, 1)){
            throw new BusinessException("???????????????");
        }

        pictureGroupMapper.addPictureGroupLikeDetail(pictureGroupId, user.getId());
    }

    @Override
    @Transactional
    @PreAuthorize("@rs.canAccessPictureGroup(#pictureGroupId)")
    public void undoLikePictureGroup(Long pictureGroupId) {
        AuthUserDto user = SecurityUtil.getNonAnonymousCurrentUser();

        if(! pictureGroupMapper.deletePictureGroupLikeDetail(pictureGroupId, user.getId())){
            throw new BusinessException("???????????????");
        }

        pictureGroupMapper.increaseLikeCount(pictureGroupId, -1);
    }

    //TODO: ***?????????

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
