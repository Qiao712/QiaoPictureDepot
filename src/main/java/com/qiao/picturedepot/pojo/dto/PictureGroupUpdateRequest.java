package com.qiao.picturedepot.pojo.dto;

import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PictureGroupUpdateRequest {
    private BigInteger pictureGroupId;
    private String title;
    private BigInteger albumId;
    private List<BigInteger> idSequence;
    private List<BigInteger> picturesToDelete;
}
