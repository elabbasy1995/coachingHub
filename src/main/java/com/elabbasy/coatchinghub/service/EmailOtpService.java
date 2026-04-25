package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.model.entity.Coachee;
import com.elabbasy.coatchinghub.model.entity.EmailOtp;
import com.elabbasy.coatchinghub.model.entity.RefreshToken;
import com.elabbasy.coatchinghub.model.entity.User;
import com.elabbasy.coatchinghub.model.enums.RoleName;
import com.elabbasy.coatchinghub.model.request.UpdateCoacheeEmailRequest;
import com.elabbasy.coatchinghub.model.response.LoginResponse;
import com.elabbasy.coatchinghub.repository.CoacheeRepository;
import com.elabbasy.coatchinghub.repository.EmailOtpRepository;
import com.elabbasy.coatchinghub.repository.UserRepository;
import com.elabbasy.coatchinghub.security.JwtUtil;
import com.elabbasy.coatchinghub.security.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmailOtpService {

    private final EmailOtpRepository otpRepository;
    private final UserRepository userRepository;
    private final CoacheeRepository coacheeRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    @Value("${app.mail.from:${spring.mail.username:}}")
    private String fromEmail;
    @Value("${app.mail.support-name:Coaching Hub}")
    private String supportName;

    public void sendOtpAfterRegistration(String email) {

//        String otp = String.valueOf(
//                ThreadLocalRandom.current().nextInt(100000, 999999));

        String otp = "1234";

        otpRepository.deleteByEmail(email);

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(email);
        emailOtp.setOtp(otp);
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(emailOtp);
//        sendEmail(
//                email,
//                "Verify your Coaching Hub account",
//                buildOtpEmailTemplate(
//                        "Welcome to Coaching Hub",
//                        "Use the verification code below to activate your account and continue your journey with us.",
//                        otp,
//                        "This code will expire in 5 minutes.",
//                        "If you did not create this account, you can safely ignore this email."
//                )
//        );
    }

    public void sendPortalAdminInvitation(String email,
                                          String fullName,
                                          String password,
                                          Collection<String> permissions) {
        StringJoiner permissionJoiner = new StringJoiner(", ");
        permissions.forEach(permissionJoiner::add);

        String subject = "Portal admin invitation";
        String body = buildAdminInvitationTemplate(fullName, email, password, permissionJoiner.toString());

        sendEmail(email, subject, body);
    }

    public void sendPortalAdminPasswordReset(String email,
                                             String fullName,
                                             String username,
                                             String password) {
        String subject = "Portal admin password reset";
        String body = buildPasswordResetTemplate(
                "Portal Admin Password Reset",
                fullName,
                username,
                password,
                "Your Coaching Hub portal admin password has been reset successfully."
        );

        sendEmail(email, subject, body);
    }

    public void sendCoachPasswordReset(String email,
                                       String fullName,
                                       String username,
                                       String password) {
        String subject = "Coach account password reset";
        String body = buildPasswordResetTemplate(
                "Coach Account Password Reset",
                fullName,
                username,
                password,
                "Your Coaching Hub coach account password has been reset successfully."
        );

        sendEmail(email, subject, body);
    }

    public void sendOtpUpdateCoacheeEmail(UpdateCoacheeEmailRequest updateCoacheeEmailRequest, Long coacheeId) {

        Coachee coachee = coacheeRepository.findById(coacheeId).orElseThrow(() -> new BusinessException(ErrorMessage.COACHEE_NOT_FOUND));

//        String otp = String.valueOf(
//                ThreadLocalRandom.current().nextInt(100000, 999999));

        String otp = "1234";

        otpRepository.deleteByEmail(coachee.getUser().getEmail());

        EmailOtp emailOtp = new EmailOtp();
        emailOtp.setEmail(coachee.getUser().getEmail());
        emailOtp.setNewEmail(updateCoacheeEmailRequest.getEmail());
        emailOtp.setUserId(coachee.getUser().getId());
        emailOtp.setOtp(otp);
        emailOtp.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        otpRepository.save(emailOtp);
//        sendEmail(
//                updateCoacheeEmailRequest.getEmail(),
//                "Confirm your new Coaching Hub email",
//                buildOtpEmailTemplate(
//                        "Confirm your new email address",
//                        "We received a request to update your Coaching Hub email address. Use this code to confirm the change.",
//                        otp,
//                        "This code will expire in 5 minutes.",
//                        "If you did not request this change, please contact support."
//                )
//        );
    }

    public LoginResponse verifyOtpAndActivate(String email, String otp) {

        EmailOtp emailOtp = otpRepository
                .findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() ->
                        new BusinessException(ErrorMessage.OTP_NOT_FOUND));

        if (emailOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorMessage.OTP_EXPIRED);
        }

        if (!emailOtp.getOtp().equals(otp)) {
            throw new BusinessException(ErrorMessage.INVALID_OTP);
        }

        emailOtp.setVerified(true);
        otpRepository.save(emailOtp);

        // ✅ Activate User
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException(ErrorMessage.USER_NOT_FOUND));
        user.setEnabled(true);
        userRepository.save(user);

        if (Objects.nonNull(user.getCoachee())) {
            // ✅ Activate Coachee
            Coachee coachee = coacheeRepository.findById(user.getCoachee().getId())
                    .orElseThrow(() ->
                            new BusinessException(ErrorMessage.COACHEE_NOT_FOUND));
            coachee.setActive(true);
            coacheeRepository.save(coachee);

            String accessToken = jwtUtil.generateAccessToken(user, RoleName.COACHEE);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

            return new LoginResponse(accessToken, refreshToken, user, RoleName.COACHEE);
        } else {
            return null;
        }
    }

    public void verifyOtpAndUpdateEmailForCoachee(Long coacheeId, String otp) {
        Coachee coachee = coacheeRepository.findById(coacheeId).orElseThrow(() -> new BusinessException(ErrorMessage.COACHEE_NOT_FOUND));
        User user = coachee.getUser();

        EmailOtp emailOtp = otpRepository
                .findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() ->
                        new BusinessException(ErrorMessage.OTP_NOT_FOUND));

        if (emailOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorMessage.OTP_EXPIRED);
        }

        if (!emailOtp.getOtp().equals(otp)) {
            throw new BusinessException(ErrorMessage.INVALID_OTP);
        }

        emailOtp.setVerified(true);
        otpRepository.save(emailOtp);

        // ✅ Activate User
        User userProfile = userRepository.findById(user.getId())
                .orElseThrow(() ->
                        new BusinessException(ErrorMessage.USER_NOT_FOUND));
        userProfile.setEmail(emailOtp.getNewEmail());
        User save = userRepository.save(userProfile);
    }

    private void sendEmail(String email, String subject, String body) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.info("Email payload generated for {} with subject [{}] and body [{}]", email, subject, body);
            return;
        }

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, "UTF-8");
            if (fromEmail != null && !fromEmail.isBlank()) {
                helper.setFrom(fromEmail, supportName);
            }
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(msg);
        } catch (Exception ex) {
            log.warn("Failed to send email to {}. Falling back to logging the email payload.", email, ex);
            log.info("Email payload generated for {} with subject [{}] and body [{}]", email, subject, body);
        }
    }

    private String buildOtpEmailTemplate(String title,
                                         String intro,
                                         String code,
                                         String note,
                                         String footerMessage) {
        return """
                <html>
                  <body style="margin:0;padding:0;background:#f4f7fb;font-family:Tahoma,Arial,sans-serif;color:#17324d;">
                    <div style="max-width:640px;margin:0 auto;padding:32px 20px;">
                      <div style="background:linear-gradient(135deg,#0f4c81,#1f7a8c);padding:28px 32px;border-radius:20px 20px 0 0;color:#ffffff;">
                        <div style="font-size:13px;letter-spacing:1.8px;text-transform:uppercase;opacity:.85;">Coaching Hub</div>
                        <h1 style="margin:12px 0 0;font-size:30px;line-height:1.2;">%s</h1>
                      </div>
                      <div style="background:#ffffff;padding:32px;border-radius:0 0 20px 20px;box-shadow:0 18px 50px rgba(15,76,129,.10);">
                        <p style="margin:0 0 18px;font-size:16px;line-height:1.8;">%s</p>
                        <div style="margin:28px 0;padding:22px;border:1px solid #d8e4f0;border-radius:18px;background:#f8fbff;text-align:center;">
                          <div style="font-size:13px;color:#5b748e;letter-spacing:1.5px;text-transform:uppercase;">Verification Code</div>
                          <div style="margin-top:10px;font-size:36px;font-weight:700;letter-spacing:8px;color:#0f4c81;">%s</div>
                        </div>
                        <p style="margin:0 0 10px;font-size:15px;line-height:1.8;color:#425b76;">%s</p>
                        <p style="margin:0;font-size:14px;line-height:1.8;color:#6b7f95;">%s</p>
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(title, intro, code, note, footerMessage);
    }

    private String buildAdminInvitationTemplate(String fullName,
                                                String email,
                                                String password,
                                                String permissions) {
        return """
                <html>
                  <body style="margin:0;padding:0;background:#f3f6fa;font-family:Tahoma,Arial,sans-serif;color:#17324d;">
                    <div style="max-width:680px;margin:0 auto;padding:32px 20px;">
                      <div style="background:linear-gradient(135deg,#17324d,#1f7a8c);padding:32px;border-radius:22px 22px 0 0;color:#ffffff;">
                        <div style="font-size:13px;letter-spacing:1.8px;text-transform:uppercase;opacity:.82;">Coaching Hub</div>
                        <h1 style="margin:14px 0 8px;font-size:30px;line-height:1.2;">Portal Admin Invitation</h1>
                        <p style="margin:0;font-size:15px;line-height:1.8;opacity:.92;">You have been invited to join the Coaching Hub portal team.</p>
                      </div>
                      <div style="background:#ffffff;padding:32px;border-radius:0 0 22px 22px;box-shadow:0 18px 50px rgba(23,50,77,.10);">
                        <p style="margin:0 0 18px;font-size:16px;line-height:1.8;">Hello %s,</p>
                        <p style="margin:0 0 24px;font-size:15px;line-height:1.8;color:#425b76;">Your admin account has been created successfully. Please use the credentials below to sign in to Coaching Hub Portal.</p>
                        <table role="presentation" style="width:100%%;border-collapse:separate;border-spacing:0 12px;">
                          <tr>
                            <td style="width:150px;font-weight:700;color:#5b748e;">Email</td>
                            <td style="background:#f8fbff;border:1px solid #d8e4f0;padding:14px 16px;border-radius:14px;">%s</td>
                          </tr>
                          <tr>
                            <td style="width:150px;font-weight:700;color:#5b748e;">Temporary Password</td>
                            <td style="background:#fff8f0;border:1px solid #f3dcc0;padding:14px 16px;border-radius:14px;font-weight:700;color:#8a4b08;">%s</td>
                          </tr>
                          <tr>
                            <td style="width:150px;font-weight:700;color:#5b748e;vertical-align:top;">Permissions</td>
                            <td style="background:#f8fbff;border:1px solid #d8e4f0;padding:14px 16px;border-radius:14px;line-height:1.8;">%s</td>
                          </tr>
                        </table>
                        <div style="margin-top:24px;padding:18px 20px;background:#eef7f3;border:1px solid #cfe5da;border-radius:16px;color:#285943;">
                          Please sign in and change your password immediately to keep your account secure.
                        </div>
                        <p style="margin:24px 0 0;font-size:14px;line-height:1.8;color:#6b7f95;">If this invitation reached you by mistake, please ignore this email or contact the Coaching Hub support team.</p>
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(fullName, email, password, permissions);
    }

    private String buildPasswordResetTemplate(String title,
                                              String fullName,
                                              String username,
                                              String password,
                                              String introMessage) {
        return """
                <html>
                  <body style="margin:0;padding:0;background:#f3f6fa;font-family:Tahoma,Arial,sans-serif;color:#17324d;">
                    <div style="max-width:680px;margin:0 auto;padding:32px 20px;">
                      <div style="background:linear-gradient(135deg,#17324d,#1f7a8c);padding:32px;border-radius:22px 22px 0 0;color:#ffffff;">
                        <div style="font-size:13px;letter-spacing:1.8px;text-transform:uppercase;opacity:.82;">Coaching Hub</div>
                        <h1 style="margin:14px 0 8px;font-size:30px;line-height:1.2;">%s</h1>
                        <p style="margin:0;font-size:15px;line-height:1.8;opacity:.92;">%s</p>
                      </div>
                      <div style="background:#ffffff;padding:32px;border-radius:0 0 22px 22px;box-shadow:0 18px 50px rgba(23,50,77,.10);">
                        <p style="margin:0 0 18px;font-size:16px;line-height:1.8;">Hello %s,</p>
                        <p style="margin:0 0 24px;font-size:15px;line-height:1.8;color:#425b76;">Please use the credentials below to sign in.</p>
                        <table role="presentation" style="width:100%%;border-collapse:separate;border-spacing:0 12px;">
                          <tr>
                            <td style="width:150px;font-weight:700;color:#5b748e;">Username</td>
                            <td style="background:#f8fbff;border:1px solid #d8e4f0;padding:14px 16px;border-radius:14px;">%s</td>
                          </tr>
                          <tr>
                            <td style="width:150px;font-weight:700;color:#5b748e;">New Password</td>
                            <td style="background:#fff8f0;border:1px solid #f3dcc0;padding:14px 16px;border-radius:14px;font-weight:700;color:#8a4b08;">%s</td>
                          </tr>
                        </table>
                        <div style="margin-top:24px;padding:18px 20px;background:#eef7f3;border:1px solid #cfe5da;border-radius:16px;color:#285943;">
                          Please sign in and change your password immediately to keep your account secure.
                        </div>
                        <p style="margin:24px 0 0;font-size:14px;line-height:1.8;color:#6b7f95;">If you did not expect this action, please contact the Coaching Hub support team.</p>
                      </div>
                    </div>
                  </body>
                </html>
                """.formatted(title, introMessage, fullName, username, password);
    }
}
