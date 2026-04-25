package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.PasswordResetOtp;
import com.elabbasy.coatchinghub.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {
    Optional<PasswordResetOtp> findFirstByUserAndOtpAndVerifiedFalseOrderByIdDesc(User user, String otp);
}