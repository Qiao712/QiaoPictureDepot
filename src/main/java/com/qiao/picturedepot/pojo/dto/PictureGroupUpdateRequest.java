package com.qiao.picturedepot.pojo.dto;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PictureGroupUpdateRequest {
    @NotNull
    private Long pictureGroupId;
    @NotBlank
    private String title;

    @Length(max = 300)
    private String description;

    @NotNull
    private Long albumId;
    @NotNull
    private List<Long> idSequence;

    private List<Long> picturesToDelete;
}
