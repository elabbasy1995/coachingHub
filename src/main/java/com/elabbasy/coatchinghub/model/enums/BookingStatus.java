package com.elabbasy.coatchinghub.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookingStatus {

    UPCOMING("Upcoming", "قادمة"),
    RUNNING("Running", "قيد التنفيذ"),
    COMPLETED("Completed", "مكتملة");

    private final String nameEn;
    private final String nameAr;
}