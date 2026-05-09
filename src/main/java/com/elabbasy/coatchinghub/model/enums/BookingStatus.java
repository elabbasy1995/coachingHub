package com.elabbasy.coatchinghub.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookingStatus {

    CANCELED("Canceled", "ملغاة"),
    NOT_CONFIRMED("Not Confirmed", "غير مؤكدة"),
    UPCOMING("Upcoming", "قادمة"),
    RUNNING("Running", "قيد التنفيذ"),
    PAST("Past", "سابقة"),
    COMPLETED("Completed", "مكتملة");

    private final String nameEn;
    private final String nameAr;
}
