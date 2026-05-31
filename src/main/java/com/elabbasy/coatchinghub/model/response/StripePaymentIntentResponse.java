package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripePaymentIntentResponse {

    private Long bookingId;
    private Long paymentId;
    private String paymentIntentId;
    private String clientSecret;
    private BigDecimal amount;
    private Long amountMinor;
    private String currency;
    private PaymentStatus status;
}
