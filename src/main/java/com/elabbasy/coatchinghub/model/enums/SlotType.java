package com.elabbasy.coatchinghub.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public enum SlotType {

    HALF_HOUR("Half Hour", "نصف ساعه", 30),
    HOUR("Hour", "ساعه", 60),
    ONE_AND_HALF_HOUR("One and Half Hour", "ساعه و نصف", 90),
    TWO_HOURS("Two Hours", "ساعتان", 120);

    private final String nameEn;
    private final String nameAr;
    private final Integer duration;
}
