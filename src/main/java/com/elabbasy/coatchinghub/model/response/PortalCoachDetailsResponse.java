package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.CountryDto;
import com.elabbasy.coatchinghub.model.NationalityDto;
import com.elabbasy.coatchinghub.model.dto.CoachingIndustryDto;
import com.elabbasy.coatchinghub.model.dto.LanguageDto;
import com.elabbasy.coatchinghub.model.dto.UserDto;
import com.elabbasy.coatchinghub.model.enums.CoachStatus;
import com.elabbasy.coatchinghub.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortalCoachDetailsResponse {

    private Long id;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime updatedDate;
    private String updatedBy;
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
    @JsonProperty("OneAndHalfHourPrice")
    private Double oneAndHalfHourPrice;
    private Double twoHoursPrice;
    private List<CoachingIndustryDto> coachingIndustries;
    private List<LanguageDto> languages;
    @JsonIgnoreProperties({"admin", "coach", "coachee"})
    private UserDto user;
    private CountryDto country;
    private NationalityDto nationality;
    private Boolean enabled;
    private Integer bookingCount;
    private List<CertificateResponse> certificates;
}


