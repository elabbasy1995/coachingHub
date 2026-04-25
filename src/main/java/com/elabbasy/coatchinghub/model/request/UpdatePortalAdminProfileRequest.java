package com.elabbasy.coatchinghub.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePortalAdminProfileRequest {

    @NotBlank(message = "FULL_NAME_IS_REQUIRED")
    private String fullName;

    @Email(message = "EMAIL_IS_REQUIRED")
    @NotBlank(message = "EMAIL_IS_REQUIRED")
    private String email;

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
