package com.elabbasy.coatchinghub.model.request;

import com.elabbasy.coatchinghub.model.enums.PortalAdminPermission;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvitePortalAdminRequest {

    @NotBlank(message = "FULL_NAME_IS_REQUIRED")
    private String fullName;

    @Email(message = "EMAIL_IS_REQUIRED")
    @NotBlank(message = "EMAIL_IS_REQUIRED")
    private String email;

    @NotEmpty(message = "PERMISSIONS_IS_REQUIRED")
    private List<PortalAdminPermission> permissions;
}
