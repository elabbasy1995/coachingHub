package com.elabbasy.coatchinghub.model.response;

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
public class PortalRevenueBetweenDatesResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private Double totalRevenue;
    private List<DailyRevenue> dailyRevenue;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyRevenue {
        private LocalDate date;
        private Double revenue;
    }
}
