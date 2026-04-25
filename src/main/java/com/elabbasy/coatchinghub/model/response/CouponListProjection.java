package com.elabbasy.coatchinghub.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CouponListProjection {

    private Long id;
    private LocalDateTime createdDate;
    private String title;
    private Integer timesOfUse;
    private Boolean unlimitedUsage;
    private String code;
    private double discountPercentage;
    private LocalDate expiryDate;
    private Long usageCount;
    private Boolean softDelete;

    public CouponListProjection(Long id,
                                LocalDateTime createdDate,
                                String title,
                                Integer timesOfUse,
                                Boolean unlimitedUsage,
                                String code,
                                double discountPercentage,
                                LocalDate expiryDate,
                                Long usageCount,
                                Boolean softDelete) {
        this.id = id;
        this.createdDate = createdDate;
        this.title = title;
        this.timesOfUse = timesOfUse;
        this.unlimitedUsage = unlimitedUsage;
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.expiryDate = expiryDate;
        this.usageCount = usageCount;
        this.softDelete = softDelete;
    }
}