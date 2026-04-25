package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByCoachId(Long coachId);
}

