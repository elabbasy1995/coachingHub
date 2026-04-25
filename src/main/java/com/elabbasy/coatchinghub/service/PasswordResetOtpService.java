package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.model.entity.PasswordResetOtp;
import com.elabbasy.coatchinghub.model.entity.User;
import com.elabbasy.coatchinghub.repository.PasswordResetOtpRepository;
import com.elabbasy.coatchinghub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordResetOtpService {

    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void sendResetPasswordOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorMessage.USER_NOT_FOUND));

//        String otp = String.format("%06d", new Random().nextInt(999999));
        String otp = "1234";

        PasswordResetOtp resetOtp = new PasswordResetOtp();
        resetOtp.setUser(user);
        resetOtp.setOtp(otp);
        resetOtp.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        passwordResetOtpRepository.save(resetOtp);

        // TODO: send OTP via email
//        emailService.sendEmail(user.getEmail(), "Password Reset OTP", "Your OTP is: " + otp);
    }

    public void verifyOtpAndResetPassword(String email, String otp, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorMessage.USER_NOT_FOUND));

        PasswordResetOtp resetOtp = passwordResetOtpRepository
                .findFirstByUserAndOtpAndVerifiedFalseOrderByIdDesc(user, otp)
                .orElseThrow(() -> new BusinessException(ErrorMessage.INVALID_OTP));

        if (resetOtp.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorMessage.OTP_EXPIRED);
        }

        // Mark OTP as verified
        resetOtp.setVerified(true);
        passwordResetOtpRepository.save(resetOtp);

        // Reset password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
