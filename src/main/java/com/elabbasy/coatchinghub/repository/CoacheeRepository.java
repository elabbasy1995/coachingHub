package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Coachee;
import com.elabbasy.coatchinghub.model.response.PortalPersonLookupResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CoacheeRepository extends JpaRepository<Coachee, Long>, CustomCoacheeRepository {

    Long countByActiveTrue();

    Long countByCreatedDateGreaterThanEqualAndCreatedDateLessThan(LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("""
        SELECT COUNT(c)
        FROM Coachee c
        WHERE (:startDateTime IS NULL OR c.createdDate >= :startDateTime)
          AND (:endDateTime IS NULL OR c.createdDate < :endDateTime)
    """)
    Long countByCreatedDateOptionalRange(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    Long countByActiveTrueAndCreatedDateGreaterThanEqualAndCreatedDateLessThan(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    @Query("""
        SELECT COUNT(c)
        FROM Coachee c
        WHERE c.active = true
          AND (:startDateTime IS NULL OR c.createdDate >= :startDateTime)
          AND (:endDateTime IS NULL OR c.createdDate < :endDateTime)
    """)
    Long countByActiveTrueAndCreatedDateOptionalRange(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("""
        SELECT new com.elabbasy.coatchinghub.model.response.PortalPersonLookupResponse(
            c.id,
            c.fullName,
            u.email
        )
        FROM Coachee c
        JOIN c.user u
        ORDER BY c.fullName ASC
    """)
    List<PortalPersonLookupResponse> findPortalCoacheeLookup();
}
