package com.elabbasy.coatchinghub.model.request;

import com.elabbasy.coatchinghub.model.enums.Language;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateCoacheeRequest {

    @NotBlank(message = "FULL_NAME_IS_REQUIRED")
    private String fullName;
    @NotNull(message = "BIRTHDATE_IS_REQUIRED")
    private LocalDate birthDate;
    @Email(message = "EMAIL_IS_REQUIRED")
    @NotBlank(message = "EMAIL_IS_REQUIRED")
    private String email;
    @NotBlank(message = "PHONE_NUMBER_REQUIRED")
    private String phoneNumber;
    @NotBlank(message = "PASSWORD_IS_REQUIRED")
    private String password;
    private Language language;

}
