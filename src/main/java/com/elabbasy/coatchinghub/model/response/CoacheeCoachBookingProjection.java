package com.elabbasy.coatchinghub.model.response;

import java.time.OffsetDateTime;

public interface CoacheeCoachBookingProjection {

    Long getId();
    OffsetDateTime getStartTime();
    OffsetDateTime getEndTime();
    Integer getPeriodMinutes();
    Double getPrice();
    Double getDiscount();
    Double getFinalPrice();

}
