package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.enums.PaymentProvider;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PortalPaymentTransactionResponse {

    private Long id;
    private Long bookingId;
    private PaymentProvider provider;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private String providerPaymentIntentId;
    private String providerCheckoutSessionId;
    private String providerChargeId;
    private String providerRefundId;
    private String providerEventId;
    private String failureReason;
    private OffsetDateTime paidAt;
    private OffsetDateTime refundedAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private OffsetDateTime bookingPaymentDateTime;
    private String bookingPaymentTransaction;
    private Long coachId;
    private String coachFullNameEn;
    private String coachFullNameAr;
    private String coachEmail;
    private Long coacheeId;
    private String coacheeFullName;
    private String coacheeEmail;
    private String coacheePhoneNumber;
}
