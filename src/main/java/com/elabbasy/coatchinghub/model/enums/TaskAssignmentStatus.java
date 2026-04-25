package com.elabbasy.coatchinghub.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TaskAssignmentStatus {

    ASSIGNED("Assigned", "تم التعيين"),
    COMPLETED("Completed", "مكتمل"),
    EXPIRED("Expired", "منتهي");

    private final String nameEn;
    private final String nameAr;
}