package com.elabbasy.coatchinghub.model;

import com.elabbasy.coatchinghub.model.dto.BaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NationalityDto extends BaseDto {

    private String code;
    private String nameEn;
    private String nameAr;
}
