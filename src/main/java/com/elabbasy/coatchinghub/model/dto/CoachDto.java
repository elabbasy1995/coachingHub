package com.elabbasy.coatchinghub.model.dto;

import com.elabbasy.coatchinghub.model.CountryDto;
import com.elabbasy.coatchinghub.model.NationalityDto;
import com.elabbasy.coatchinghub.model.enums.CoachStatus;
import com.elabbasy.coatchinghub.model.enums.EnglishLevel;
import com.elabbasy.coatchinghub.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class CoachDto extends AuditBaseDto {

    private String fullNameEn;
    private String fullNameAr;
    private Gender gender;
    private LocalDate birthDate;
    private String email;
    private String whatsAppNumber;

    private Integer yearsOfExperience;
    private Boolean availableEveryWeek;
    private String jobTitle;

    private String username;
    private String profileImageUrl;

    private CoachStatus status;

    private Double halfHourPrice;
    private Double hourlyPrice;
    private Double halfAndHourPrice;
    private Double twoHoursPrice;

    private List<CoachingIndustryDto> coachingIndustries;
    private List<LanguageDto> languages;

    @JsonIgnoreProperties({"admin", "coach", "coachee"})
    private UserDto user;

    private CountryDto country;
    private NationalityDto nationality;
    private Integer bookingCount;
}
