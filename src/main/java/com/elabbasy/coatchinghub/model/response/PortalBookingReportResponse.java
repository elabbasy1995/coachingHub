package com.elabbasy.coatchinghub.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortalBookingReportResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalCount;
    private Long completedCount;
    private Long notCompletedCount;
}
