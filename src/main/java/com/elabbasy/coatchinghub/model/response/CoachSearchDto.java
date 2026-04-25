package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.NationalityDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CoachSearchDto {
    private Long id;
    private String fullNameEn;
    private String fullNameAr;
    private String profileImageUrl;
    private Integer yearsOfExperience;
    private NationalityDto nationality;
    private List<Industry> industries;
    private Long bookingCount;


    @SneakyThrows
    public CoachSearchDto(Long id,
                          String fullNameEn,
                          String fullNameAr,
                          String profileImageUrl,
                          Integer yearsOfExperience,
                          Long nationalityId,
                          String nationalityNameEn,
                          String nationalityNameAr,
                          String industriesJson,
                          Long bookingCount
    ) {
        this.id = id;
        this.fullNameEn = fullNameEn;
        this.fullNameAr = fullNameAr;
        this.profileImageUrl = profileImageUrl;
        this.yearsOfExperience = yearsOfExperience;
        this.bookingCount = bookingCount;
        this.nationality = mapNationality(nationalityId, nationalityNameEn, nationalityNameAr);

        ObjectMapper mapper = new ObjectMapper();
        this.industries =
                mapper.readValue(industriesJson,
                        new TypeReference<List<Industry>>() {});
        if (Objects.nonNull(industries) && industries.size() == 1 && industries.get(0).getNameEn() == null && industries.get(0).getNameAr() == null) {
            industries = new ArrayList<>();
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Industry {
        private Long id;
        private String nameEn;
        private String nameAr;
    }
}
