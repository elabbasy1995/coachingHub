package com.elabbasy.coatchinghub.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleBookingRequest {

    @NotNull(message = "BOOKING_REQUIRED")
    private Long bookingId;

    @NotNull(message = "SLOT_IS_REQUIRED")
    private Long coachSlotId;
}
