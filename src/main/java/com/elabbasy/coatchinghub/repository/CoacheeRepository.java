package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Coachee;
import com.elabbasy.coatchinghub.model.response.PortalPersonLookupResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CoacheeRepository extends JpaRepository<Coachee, Long>, CustomCoacheeRepository {

    Long countByActiveTrue();

    Long countByCreatedDateGreaterThanEqualAndCreatedDateLessThan(LocalDateTime startDateTime, LocalDateTime endDateTime);

    Long countByActiveTrueAndCreatedDateGreaterThanEqualAndCreatedDateLessThan(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
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
