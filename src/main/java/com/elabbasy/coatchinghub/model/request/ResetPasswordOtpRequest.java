package com.elabbasy.coatchinghub.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordOtpRequest {
    private String email;
    private String otp;
    private String newPassword;
}
