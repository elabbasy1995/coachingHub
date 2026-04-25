package com.elabbasy.coatchinghub.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApproveCoachRequest {

    private Double halfHourPrice;
    private Double hourlyPrice;
    private Double OneAndHalfHourPrice;
    private Double twoHoursPrice;
}
