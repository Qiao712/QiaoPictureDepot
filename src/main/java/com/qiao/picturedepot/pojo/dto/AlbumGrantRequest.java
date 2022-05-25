package com.qiao.picturedepot.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AlbumGrantRequest {
    @NotNull
    private Long albumId;
    private List<Long> friendGroupIdsGranted;
    private List<Long> friendGroupIdsRevoked;
}
