package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.enums.PaymentStatus;

import java.time.OffsetDateTime;

public interface CoachBookingProjection {

    Long getId();
    OffsetDateTime getStartTime();
    OffsetDateTime getEndTime();
    Integer getPeriodMinutes();
    Double getPrice();
    Double getDiscount();
    Double getFinalPrice();
    PaymentStatus getPaymentStatus();
    String getCoacheeFullName();
    String getCoacheeProfileImageUrl();

}
