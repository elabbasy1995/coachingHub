package com.elabbasy.coatchinghub.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CoachStatus {

    STEP1_COMPLETED("Step 1 Completed", "اكتمال الخطوة 1"),
    STEP2_COMPLETED("Step 2 Completed", "اكتمال الخطوة 2"),
    STEP3_COMPLETED("Step 3 Completed", "اكتمال الخطوة 3"),
    PENDING_APPROVAL("Pending Approval", "في انتظار الموافقة"),
    SUBMITTED("Submitted", "تم الإرسال"),
    APPROVED("Approved", "تم الموافقة"),
    REJECTED("Rejected", "مرفوض");

    private final String nameEn;
    private final String nameAr;
}
