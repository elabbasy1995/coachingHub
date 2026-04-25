package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.EmailOtp;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long> {

    @Modifying
    @Transactional
    void deleteByEmail(String email);

    Optional<EmailOtp> findTopByEmailOrderByCreatedAtDesc(String email);

    Optional<EmailOtp> findTopByUserIdOrderByCreatedAtDesc(Long userId);

}
