package com.elabbasy.coatchinghub.model.entity;

import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import com.elabbasy.coatchinghub.model.enums.SlotType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLRestriction("DELETED <> true")
@SQLDelete(sql = "UPDATE {h-schema} bookings SET DELETED = true where id = ?")
public class Booking extends AuditBaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coach_id", nullable = false)
    private Coach coach;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coachee_id", nullable = false)
    private Coachee coachee;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coach_slot_id")
    private CoachSlot coachSlot;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;
    @Column(name = "period_minutes", nullable = false)
    private Integer periodMinutes;
    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;
    @Column(name = "slot_type")
    @Enumerated(EnumType.STRING)
    private SlotType slotType;
    @Column(name = "price", nullable = false)
    private Double price;
    @Column(name = "discount")
    private Double discount;
    @Column(name = "final_price")
    private Double finalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    @Column(name = "payment_datetime")
    private OffsetDateTime paymentDateTime;
    @Column(name = "payment_transaction", length = 100)
    private String paymentTransaction;
}
