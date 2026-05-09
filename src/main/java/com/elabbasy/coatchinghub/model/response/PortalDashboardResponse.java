package com.elabbasy.coatchinghub.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortalDashboardResponse {

    private CoachStats coaches;
    private CoacheeStats coachees;
    private BookingStats bookings;
    private RevenueStats revenue;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CoachStats {
        private Long total;
        private Long approved;
        private Long pendingApproval;
        private Long rejected;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CoacheeStats {
        private Long total;
        private Long active;
        private Long inactive;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingStats {
        private Long total;
        private Long paid;
        private Long notConfirmed;
        private Long cancelled;
        private Long refunded;
        private Long today;
        private Long thisMonth;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RevenueStats {
        private Double total;
        private Double today;
        private Double thisMonth;
    }
}
