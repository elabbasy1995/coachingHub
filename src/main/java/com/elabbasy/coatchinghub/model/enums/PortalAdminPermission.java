package com.elabbasy.coatchinghub.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PortalAdminPermission {
    ADMINS("Admins", "المشرفين"),
    COACHES("Coaches", "المدربين"),
    BOOKING("Booking", "الحجوزات"),
    TRANSACTIONS("Transactions", "المعاملات المالية"),
    COUPONS("Coupons", "الكوبونات"),
    GATEWAYS("Gateways", "بوابات الدفع"),
    REPORTS("Reports", "التقارير");

    private final String nameEn;
    private final String nameAr;
}
