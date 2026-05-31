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
    private List<MonthlyRevenue> monthlyRevenue;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyRevenue {
        private LocalDate month;
        private String monthNameEn;
        private String monthNameAr;
        private Double revenue;
    }
}
