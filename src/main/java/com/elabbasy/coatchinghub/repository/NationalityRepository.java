package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NationalityRepository extends JpaRepository<Nationality, Long> {

    List<Nationality> findByActiveTrueOrderByNameEnAsc();

    Optional<Nationality> findByCode(String code);
}
