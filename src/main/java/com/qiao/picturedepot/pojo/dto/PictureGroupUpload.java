package com.qiao.picturedepot.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PictureGroupUpload {
    private BigInteger pictureGroupId;
    private String title;
    private BigInteger albumId;
    private List<BigInteger> idSequence;
    private List<BigInteger> picturesToDelete;
}
