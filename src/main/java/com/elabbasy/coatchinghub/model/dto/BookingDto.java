package com.elabbasy.coatchinghub.model.dto;

import com.elabbasy.coatchinghub.model.enums.MeetingProvider;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
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
    private BookingFormAnswersDto formAnswers;
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private OffsetDateTime paymentDateTime;
    private String paymentTransaction;
    private MeetingProvider meetingProvider;
    private String meetingId;
    private String meetingRoomUrl;
    private String meetingHostRoomUrl;
    private OffsetDateTime meetingCreatedAt;
    private OffsetDateTime meetingDeletedAt;
}
