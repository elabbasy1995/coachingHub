package com.elabbasy.coatchinghub.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortalIndustryPaidBookingCountResponse {

    private Long industryId;
    private String industryNameEn;
    private String industryNameAr;
    private Long paidBookingCount;
}
