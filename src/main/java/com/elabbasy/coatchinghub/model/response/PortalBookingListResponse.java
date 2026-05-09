package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.dto.BookingFormAnswersDto;
import com.elabbasy.coatchinghub.model.enums.BookingStatus;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import com.elabbasy.coatchinghub.model.enums.SlotType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PortalBookingListResponse {
    private Long id;
    private Long coachId;
    private Long coacheeId;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Integer periodMinutes;
    private SlotType slotType;
    private Double price;
    private Double discount;
    private Double finalPrice;
    private BookingFormAnswersDto formAnswers;
    private PaymentStatus paymentStatus;
    private OffsetDateTime paymentDateTime;
    private String paymentTransaction;
    private String coachFullNameEn;
    private String coachFullNameAr;
    private String coachEmail;
    private String coacheeFullName;
    private String coacheeEmail;
    private String coacheePhoneNumber;
    private BookingStatus bookingStatus;

    public PortalBookingListResponse(Long id,
                                     Long coachId,
                                     Long coacheeId,
                                     OffsetDateTime startTime,
                                     OffsetDateTime endTime,
                                     Integer periodMinutes,
                                     SlotType slotType,
                                     Double price,
                                     Double discount,
                                     Double finalPrice,
                                     BookingFormAnswersDto formAnswers,
                                     PaymentStatus paymentStatus,
                                     OffsetDateTime paymentDateTime,
                                     String paymentTransaction,
                                     String coachFullNameEn,
                                     String coachFullNameAr,
                                     String coachEmail,
                                     String coacheeFullName,
                                     String coacheeEmail,
                                     String coacheePhoneNumber) {
        this.id = id;
        this.coachId = coachId;
        this.coacheeId = coacheeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.periodMinutes = periodMinutes;
        this.slotType = slotType;
        this.price = price;
        this.discount = discount;
        this.finalPrice = finalPrice;
        this.formAnswers = formAnswers;
        this.paymentStatus = paymentStatus;
        this.paymentDateTime = paymentDateTime;
        this.paymentTransaction = paymentTransaction;
        this.coachFullNameEn = coachFullNameEn;
        this.coachFullNameAr = coachFullNameAr;
        this.coachEmail = coachEmail;
        this.coacheeFullName = coacheeFullName;
        this.coacheeEmail = coacheeEmail;
        this.coacheePhoneNumber = coacheePhoneNumber;
    }
}
