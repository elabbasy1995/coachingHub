package com.elabbasy.coatchinghub.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CoachingIndustryDto {
    private Long id;
    private String nameEn;
    private String nameAr;
}