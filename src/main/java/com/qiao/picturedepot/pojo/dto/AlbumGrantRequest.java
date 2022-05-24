package com.qiao.picturedepot.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class AlbumGrantRequest {
    private Long albumId;
    private List<Long> friendGroupIdsGranted;
    private List<Long> friendGroupIdsRevoked;
}
