package com.elabbasy.coatchinghub.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LanguageDto extends BaseDto {

    private String code;
    private String nameEn;
    private String nameAr;
}
