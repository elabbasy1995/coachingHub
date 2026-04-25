package com.elabbasy.coatchinghub.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Gender {

    MALE("Male","ذكر"),
    FEMALE("Female","أنثى");

    private final String nameEn;
    private final String nameAr;
}
