package com.elabbasy.coatchinghub.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuestionType {

    TEXT("Text", "نصي"),
    MULTIPLE_CHOICE("Multiple Choice", "اختيار متعدد");

    private final String nameEn;
    private final String nameAr;
}
