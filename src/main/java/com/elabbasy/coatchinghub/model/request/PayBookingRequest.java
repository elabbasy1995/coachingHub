package com.elabbasy.coatchinghub.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayBookingRequest {

    @NotNull(message = "BOOKING_REQUIRED")
    private Long bookingId;
    private String transactionId;

}
