package com.qiao.picturedepot.pojo.dto;

import com.qiao.picturedepot.pojo.domain.Album;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlbumDto extends Album {
    /**
     * 授权/撤销对好友分组的授权
     */
    private List<Long> friendGroupIdsGranted;
    private List<Long> friendGroupIdsRevoked;
}
