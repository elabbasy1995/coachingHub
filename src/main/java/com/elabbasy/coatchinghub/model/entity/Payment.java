package com.elabbasy.coatchinghub.model.entity;

import com.elabbasy.coatchinghub.model.enums.PaymentProvider;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} payments SET DELETED = true where id = ?")
public class Payment extends AuditBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 50)
    private PaymentProvider provider = PaymentProvider.STRIPE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "provider_payment_intent_id", length = 255, unique = true)
    private String providerPaymentIntentId;

    @Column(name = "provider_checkout_session_id", length = 255, unique = true)
    private String providerCheckoutSessionId;

    @Column(name = "provider_charge_id", length = 255)
    private String providerChargeId;

    @Column(name = "provider_refund_id", length = 255)
    private String providerRefundId;

    @Column(name = "provider_event_id", length = 255)
    private String providerEventId;

    @Column(name = "failure_reason", length = 1000)
    private String failureReason;

    @Column(name = "paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "refunded_at")
    private OffsetDateTime refundedAt;
}
