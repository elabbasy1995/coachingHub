package com.elabbasy.coatchinghub.model.dto;

import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto extends AuditBaseDto {

    private CoachDto coach;
    private CoacheeDto coachee;
    private CoachSlotDto coachSlot;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Integer periodMinutes;
    private Double price;
    private Double discount;
    private Double finalPrice;
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private OffsetDateTime paymentDateTime;
    private String paymentTransaction;
}
