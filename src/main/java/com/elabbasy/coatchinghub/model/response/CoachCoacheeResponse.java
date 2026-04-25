package com.elabbasy.coatchinghub.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoachCoacheeResponse {

    private Long id;
    private String fullName;
    private LocalDate birthDate;
    private String profileImageUrl;
    private Long bookingCount;
    private OffsetDateTime lastBookingDate;

}
