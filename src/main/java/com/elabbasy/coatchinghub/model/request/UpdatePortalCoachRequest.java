package com.elabbasy.coatchinghub.model.request;

import com.elabbasy.coatchinghub.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class UpdatePortalCoachRequest {

    @NotBlank(message = "FULL_NAME_IS_REQUIRED")
    private String fullNameEn;

    @NotBlank(message = "FULL_NAME_IS_REQUIRED")
    private String fullNameAr;

    @NotNull(message = "GENDER_IS_REQUIRED")
    private Gender gender;

    @NotNull(message = "BIRTHDATE_IS_REQUIRED")
    private LocalDate birthDate;

    @NotNull(message = "COUNTRY_REQUIRED")
    private Long countryId;

    @NotNull(message = "NATIONALITY_REQUIRED")
    private Long nationalityId;

    @NotBlank(message = "EMAIL_IS_REQUIRED")
    private String email;

    @NotBlank(message = "PHONE_NUMBER_REQUIRED")
    private String whatsAppNumber;

    @NotNull(message = "YEARS_OF_EXPERIENCE_IS_REQUIRED")
    private Integer yearsOfExperience;

    @NotEmpty(message = "LANGUAGES_IS_REQUIRED")
    private List<Long> languageIds;

    @NotNull(message = "AVAILABILITY_CHECK_IS_REQUIRED")
    private Boolean availableEveryWeek;

    @NotBlank(message = "JOB_TITLE_REQUIRED")
    private String jobTitle;

    @NotEmpty(message = "COACHING_INDUSTRIES_REQUIRED")
    private List<Long> coachingIndustriesIds;

    @NotBlank(message = "USERNAME_REQUIRED")
    private String username;

    @NotNull(message = "HALF_HOUR_PRICE_REQUIRED")
    private Double halfHourPrice;

    @NotNull(message = "HOURLY_PRICE_REQUIRED")
    private Double hourlyPrice;

    @NotNull(message = "ONE_AND_HALF_HOUR_PRICE_REQUIRED")
    private Double oneAndHalfHourPrice;

    @NotNull(message = "TWO_HOURS_PRICE_REQUIRED")
    private Double twoHoursPrice;

    @Valid
    private Attachment profileImage;

    @Valid
    private List<Attachment> certificates;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attachment {
        private Long id;

        @JsonAlias("attachmentName")
        private String name;

        private String fileUrl;
        private String contentType;
        private String content;

        public boolean hasContent() {
            return content != null && !content.isBlank();
        }

        public boolean hasFileUrl() {
            return fileUrl != null && !fileUrl.isBlank();
        }
    }
}

