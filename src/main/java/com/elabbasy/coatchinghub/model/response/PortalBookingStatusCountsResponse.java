package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortalBookingStatusCountsResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private Long totalCount;
    private List<StatusCount> statusCounts;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatusCount {
        private BookingStatus status;
        private String nameEn;
        private String nameAr;
        private Long count;
    }
}
