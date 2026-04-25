package com.elabbasy.coatchinghub.controller;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.model.entity.RefreshToken;
import com.elabbasy.coatchinghub.model.entity.User;
import com.elabbasy.coatchinghub.model.enums.CoachStatus;
import com.elabbasy.coatchinghub.model.enums.RoleName;
import com.elabbasy.coatchinghub.model.request.AuthRequest;
import com.elabbasy.coatchinghub.model.request.RefreshTokenRequest;
import com.elabbasy.coatchinghub.model.request.ResetPasswordOtpRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.LoginResponse;
import com.elabbasy.coatchinghub.model.response.RefreshTokenResponse;
import com.elabbasy.coatchinghub.repository.CoachRepository;
import com.elabbasy.coatchinghub.repository.UserRepository;
import com.elabbasy.coatchinghub.security.CustomUserDetailsService;
import com.elabbasy.coatchinghub.security.JwtUtil;
import com.elabbasy.coatchinghub.security.RefreshTokenService;
import com.elabbasy.coatchinghub.service.EmailOtpService;
import com.elabbasy.coatchinghub.service.PasswordResetOtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth APIs", description = "Authentication Apis")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final PasswordResetOtpService passwordResetOtpService;
    private final EmailOtpService emailOtpService;
    private final CoachRepository coachRepository;

    @Operation(summary = "login api")
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody AuthRequest authRequest) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmail(), authRequest.getPassword()
                )
        );

        User user = userRepository.findDetailedByEmail(authRequest.getEmail()).orElseThrow();
        if (!user.isEnabled()) {
            emailOtpService.sendOtpAfterRegistration(user.getEmail());
            throw new BusinessException(ErrorMessage.USER_INACTIVE);
        }
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            boolean hasRole = user.getRoles().stream()
                    .anyMatch(role -> role.getName().equals(authRequest.getRoleName().name()));
            if (!hasRole)
                throw new BusinessException(ErrorMessage.INVALID_ROLE);
        } else throw new BusinessException(ErrorMessage.INVALID_ROLE);

        if (authRequest.getRoleName().equals(RoleName.COACH) && !CoachStatus.APPROVED.equals(user.getCoach().getStatus())) {
            throw new BusinessException(ErrorMessage.ACCOUNT_IS_PENDING_APPROVAL);
        }
        String accessToken = jwtUtil.generateAccessToken(user, authRequest.getRoleName());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new ApiResponse<>(new LoginResponse(accessToken, refreshToken, user, authRequest.getRoleName()));
    }

    @Operation(summary = "refresh token api")
    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshTokenStr = refreshTokenRequest.getRefreshToken();

        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();
        RoleName activeRole = jwtUtil.resolvePrimaryRole(user);
        String newAccessToken = jwtUtil.generateAccessToken(user, activeRole);

        return new ApiResponse<>(new RefreshTokenResponse(newAccessToken, refreshToken.getToken()));
    }

    @PostMapping("/forgot-password/send-otp")
    public ApiResponse<String> sendOtp(@RequestParam String email) {
        passwordResetOtpService.sendResetPasswordOtp(email);
        return new ApiResponse<>("OTP has been sent to your email");
    }

    @PostMapping("/forgot-password/verify-otp")
    public ApiResponse<String> verifyOtp(@RequestBody ResetPasswordOtpRequest request) {
        passwordResetOtpService.verifyOtpAndResetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        return new ApiResponse<>("Password has been reset successfully");
    }
}
