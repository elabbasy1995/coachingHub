package com.elabbasy.coatchinghub.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookingAction {

    START("Start", "ابدأ"),
    CANCEL("Cancel", "إلغاء");

    private final String nameEn;
    private final String nameAr;
}