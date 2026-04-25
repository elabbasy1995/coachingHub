package com.elabbasy.coatchinghub.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequest {

    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}