package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class CoachBookingResponse {
    private Long id;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private Integer periodMinutes;
    private Double price;
    private Double discount;
    private Double finalPrice;
    private String coacheeFullName;
    private String coacheeProfileImageUrl;

    private List<ActionDto> actions;
    private BookingStatus status;
    private StatusWrapper statusWrapper;
}