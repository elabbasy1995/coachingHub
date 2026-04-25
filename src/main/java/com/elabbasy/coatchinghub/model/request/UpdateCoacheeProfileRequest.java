package com.elabbasy.coatchinghub.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCoacheeProfileRequest {

    @NotBlank(message = "PHONE_NUMBER_REQUIRED")
    private String phoneNumber;
    @NotBlank(message = "FULL_NAME_IS_REQUIRED")
    private String fullName;
}
