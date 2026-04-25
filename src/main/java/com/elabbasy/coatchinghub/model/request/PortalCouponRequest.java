package com.elabbasy.coatchinghub.model.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PortalCouponRequest {

    @NotBlank(message = "TITLE_IS_REQUIRED")
    private String title;

    private Integer timesOfUse;

    @NotNull(message = "MISSING_REQUIRED_ATTRIBUTES")
    private Boolean unlimitedUsage;

    @NotNull(message = "MISSING_REQUIRED_ATTRIBUTES")
    private Boolean allCoaches;

    @NotBlank(message = "CODE_IS_REQUIRED")
    private String code;

    @NotNull(message = "MISSING_REQUIRED_ATTRIBUTES")
    @DecimalMin(value = "0.0", inclusive = false, message = "MISSING_REQUIRED_ATTRIBUTES")
    @DecimalMax(value = "100.0", inclusive = true, message = "MISSING_REQUIRED_ATTRIBUTES")
    private Double discountPercentage;

    @NotNull(message = "MISSING_REQUIRED_ATTRIBUTES")
    private LocalDate expiryDate;

    private List<Long> coachIds;
}
