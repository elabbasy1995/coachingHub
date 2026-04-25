package com.elabbasy.coatchinghub.controller.mobile;

import com.elabbasy.coatchinghub.constant.Constants;
import com.elabbasy.coatchinghub.model.request.UpdateCoacheeEmailRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.LoginResponse;
import com.elabbasy.coatchinghub.service.EmailOtpService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/public/otp")
public class EmailOtpController {

    private final EmailOtpService otpService;

    public EmailOtpController(EmailOtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/verify")
    public ApiResponse<?> verify(
            @RequestParam String email,
            @RequestParam String otp) {

        LoginResponse loginResponse = otpService.verifyOtpAndActivate(email, otp);
        if (Objects.nonNull(loginResponse)) {
            return new ApiResponse<>(loginResponse);
        } else {
            return new ApiResponse<>(HttpStatus.OK, "200", "Account verification is done. your account is under approval",
                    "تم التحقق من الحساب. حسابك قيد الموافقة.");
        }
    }

    @PutMapping("/update-coachee-email")
    public ApiResponse<?> updateCoacheeEmail(@RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId,
                                             @RequestBody @Valid UpdateCoacheeEmailRequest updateCoacheeEmailRequest) {
        otpService.sendOtpUpdateCoacheeEmail(updateCoacheeEmailRequest, coacheeId);

        return new ApiResponse<>(HttpStatus.OK, "200", "Please validate the new email otp to confirm update email",
                "يرجى التحقق من رمز التحقق المرسل إلى بريدك الإلكتروني الجديد لتأكيد تحديث البريد الإلكتروني");
    }

    @PostMapping("/verify-update-coachee-email")
    public ApiResponse<?> verifyUpdateCoacheeEmail(
            @RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId,
            @RequestParam String otp) {

        otpService.verifyOtpAndUpdateEmailForCoachee(coacheeId, otp);

        return new ApiResponse<>(HttpStatus.OK, "200", "Account verification is done.",
                "تم التحقق من الحساب.");

    }
}
