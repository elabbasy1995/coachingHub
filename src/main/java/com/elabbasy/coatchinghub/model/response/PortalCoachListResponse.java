package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.NationalityDto;
import com.elabbasy.coatchinghub.model.dto.CoachingIndustryDto;
import com.elabbasy.coatchinghub.model.enums.CoachStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PortalCoachListResponse {

    private Long id;
    private String fullNameAr;
    private String fullNameEn;
    private CoachStatus status;
    private Double halfHourPrice;
    private Double hourlyPrice;
    @JsonProperty("OneAndHalfHourPrice")
    private Double oneAndHalfHourPrice;
    private Double twoHoursPrice;
    private Boolean enabled;
    private NationalityDto nationality;
    private List<CoachingIndustryDto> coachingIndustries;
    private Long bookingCount;

    @SneakyThrows
    public PortalCoachListResponse(Long id,
                                   String fullNameAr,
                                   String fullNameEn,
                                   CoachStatus status,
                                   Double halfHourPrice,
                                   Double hourlyPrice,
                                   Double oneAndHalfHourPrice,
                                   Double twoHoursPrice,
                                   Boolean enabled,
                                   Long nationalityId,
                                   String nationalityNameEn,
                                   String nationalityNameAr,
                                   String coachingIndustriesJson,
                                   Long bookingCount) {
        this.id = id;
        this.fullNameAr = fullNameAr;
        this.fullNameEn = fullNameEn;
        this.status = status;
        this.halfHourPrice = halfHourPrice;
        this.hourlyPrice = hourlyPrice;
        this.oneAndHalfHourPrice = oneAndHalfHourPrice;
        this.twoHoursPrice = twoHoursPrice;
        this.enabled = enabled;
        this.bookingCount = bookingCount;
        this.nationality = mapNationality(nationalityId, nationalityNameEn, nationalityNameAr);

        ObjectMapper mapper = new ObjectMapper();
        this.coachingIndustries = mapper.readValue(
                coachingIndustriesJson,
                new TypeReference<List<CoachingIndustryDto>>() {}
        );

        if (Objects.nonNull(coachingIndustries)
                && coachingIndustries.size() == 1
                && coachingIndustries.get(0).getNameEn() == null
                && coachingIndustries.get(0).getNameAr() == null) {
            coachingIndustries = new ArrayList<>();
        }
    }

    private NationalityDto mapNationality(Long nationalityId, String nationalityNameEn, String nationalityNameAr) {
        if (nationalityId == null) {
            return null;
        }

        NationalityDto dto = new NationalityDto();
        dto.setId(nationalityId);
        dto.setNameEn(nationalityNameEn);
        dto.setNameAr(nationalityNameAr);
        return dto;
    }
}
