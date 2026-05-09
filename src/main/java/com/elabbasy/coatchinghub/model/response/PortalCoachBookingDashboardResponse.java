package com.elabbasy.coatchinghub.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortalCoachBookingDashboardResponse {

    private Long coachId;
    private String coachFullNameEn;
    private String coachFullNameAr;
    private Long bookingCount;
    private Double totalRevenue;
    private Long cancelledBookingCount;
    private Long upcomingBookingCount;
    private Long completedBookingCount;
    private Long lostCoacheeCount;
}
