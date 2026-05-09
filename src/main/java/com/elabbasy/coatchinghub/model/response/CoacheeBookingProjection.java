package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.enums.PaymentStatus;

import java.time.OffsetDateTime;
import java.util.List;

public interface CoacheeBookingProjection {

    Long getId();
    OffsetDateTime getStartTime();
    OffsetDateTime getEndTime();
    Integer getPeriodMinutes();
    Double getPrice();
    Double getDiscount();
    Double getFinalPrice();
    PaymentStatus getPaymentStatus();
    Long getCoachId();
    String getCoachFullNameEn();
    String getCoachFullNameAr();
    String getCoachProfileImageUrl();
}
