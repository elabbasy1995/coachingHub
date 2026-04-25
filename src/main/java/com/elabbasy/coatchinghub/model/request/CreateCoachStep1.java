package com.elabbasy.coatchinghub.model.request;

import com.elabbasy.coatchinghub.model.enums.Gender;
import com.elabbasy.coatchinghub.model.enums.Language;
import jakarta.validation.Valid;
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
public class CreateCoachStep1 {

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
    @NotNull(message = "LANGUAGES_IS_REQUIRED")
    private List<Long> languageIds;
    @NotNull(message = "AVAILABILITY_CHECK_IS_REQUIRED")
    private Boolean availableEveryWeek;
    @NotNull(message = "JOB_TITLE_REQUIRED")
    private String jobTitle;
    @NotNull(message = "COACHING_INDUSTRIES_REQUIRED")
    private List<Long> coachingIndustriesIds;
    @NotBlank(message = "USERNAME_REQUIRED")
    private String username;
    @NotBlank(message = "PASSWORD_IS_REQUIRED")
    private String password;
    private Language language;

    @Valid
    @NotNull(message = "PROFILE_IMAGE_REQUIRED")
    private CreateCoachStep1.Attachment profileImage;

    @Valid
    private List<CreateCoachStep1.Attachment> certificates;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Attachment {
        @NotNull(message = "CONTENT_TYPE_REQUIRED")
        private String contentType;
        @NotNull(message = "CONTENT_REQUIRED")
        private String content;
        @NotNull(message = "ATTACHMENT_NAME_REQUIRED")
        private String attachmentName;
    }

}
