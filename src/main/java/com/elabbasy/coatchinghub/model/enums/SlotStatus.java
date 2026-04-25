package com.elabbasy.coatchinghub.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SlotStatus {
    AVAILABLE("Available", "متاح"),
    BOOKED("Booked", "محجوز"),
    CANCELED("Canceled", "ألغيت");

    private final String nameEn;
    private final String nameAr;
}

