package com.elabbasy.coatchinghub.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EnglishLevel {

    BEGINNER("Beginner", "مبتدئ"),
    ELEMENTARY("Elementary", "أساسي"),
    INTERMEDIATE("Intermediate", "متوسط"),
    UPPER_INTERMEDIATE("Upper-Intermediate", "متوسط متقدم"),
    ADVANCED("Advanced", "متقدم"),
    FLUENT("Fluent", "بطلاقة");

    private final String nameEn;
    private final String nameAr;
}
