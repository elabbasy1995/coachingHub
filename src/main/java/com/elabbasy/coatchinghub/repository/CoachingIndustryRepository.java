package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.CoachingIndustry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachingIndustryRepository extends JpaRepository<CoachingIndustry, Long> {

    @Query("""
    SELECT c.id, ci
    FROM Coach c
    JOIN c.coachingIndustries ci
    WHERE c.id IN :coachIds
""")
    List<Object[]> findIndustriesByCoachIds(List<Long> coachIds);
}
