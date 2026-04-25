package com.elabbasy.coatchinghub.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortalCouponResponse {

    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String title;
    private Integer timesOfUse;
    private Boolean unlimitedUsage;
    private Boolean allCoaches;
    private String code;
    private Double discountPercentage;
    private LocalDate expiryDate;
    private Long usageCount;
    private Boolean softDelete;
    private List<CoachSummary> coaches;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CoachSummary {
        private Long id;
        private String fullNameEn;
        private String fullNameAr;
    }
}
