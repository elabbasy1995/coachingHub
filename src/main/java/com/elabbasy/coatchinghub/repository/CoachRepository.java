package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Coach;
import com.elabbasy.coatchinghub.model.enums.CoachStatus;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import com.elabbasy.coatchinghub.model.response.PortalCoachBookingDashboardResponse;
import com.elabbasy.coatchinghub.model.response.PortalCoachLookupProjection;
import com.elabbasy.coatchinghub.model.response.PortalPersonLookupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;


@Repository
public interface CoachRepository extends JpaRepository<Coach, Long>, CustomCoachRepository {

    Long countByStatus(CoachStatus status);

    Long countByCreatedDateGreaterThanEqualAndCreatedDateLessThan(LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("""
        SELECT COUNT(c)
        FROM Coach c
        WHERE (:startDateTime IS NULL OR c.createdDate >= :startDateTime)
          AND (:endDateTime IS NULL OR c.createdDate < :endDateTime)
    """)
    Long countByCreatedDateOptionalRange(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    Long countByStatusAndCreatedDateGreaterThanEqualAndCreatedDateLessThan(
            CoachStatus status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    @Query("""
        SELECT COUNT(c)
        FROM Coach c
        WHERE c.status = :status
          AND (:startDateTime IS NULL OR c.createdDate >= :startDateTime)
          AND (:endDateTime IS NULL OR c.createdDate < :endDateTime)
    """)
    Long countByStatusAndCreatedDateOptionalRange(
            @Param("status") CoachStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    List<PortalCoachLookupProjection> findByStatusOrderByFullNameEnAsc(CoachStatus status);

    @Query("""
        SELECT new com.elabbasy.coatchinghub.model.response.PortalPersonLookupResponse(
            c.id,
            c.fullNameEn,
            u.email
        )
        FROM Coach c
        JOIN c.user u
        ORDER BY c.fullNameEn ASC
    """)
    List<PortalPersonLookupResponse> findPortalCoachLookup();

    @Query(value = """
        SELECT new com.elabbasy.coatchinghub.model.response.PortalCoachBookingDashboardResponse(
            c.id,
            c.fullNameEn,
            c.fullNameAr,
            (
                SELECT COUNT(b)
                FROM Booking b
                WHERE b.coach = c
                  AND (:hasStartDate = false OR b.startTime >= :startDateTime)
                  AND (:hasEndDate = false OR b.startTime < :endDateTime)
            ),
            (
                SELECT COALESCE(SUM(b.finalPrice), 0)
                FROM Booking b
                WHERE b.coach = c
                  AND b.paymentStatus = :paidStatus
                  AND (:hasStartDate = false OR b.startTime >= :startDateTime)
                  AND (:hasEndDate = false OR b.startTime < :endDateTime)
            ),
            (
                SELECT COUNT(b)
                FROM Booking b
                WHERE b.coach = c
                  AND b.paymentStatus = :cancelledStatus
                  AND (:hasStartDate = false OR b.startTime >= :startDateTime)
                  AND (:hasEndDate = false OR b.startTime < :endDateTime)
            ),
            (
                SELECT COUNT(b)
                FROM Booking b
                WHERE b.coach = c
                  AND b.paymentStatus NOT IN (:pendingStatus, :cancelledStatus)
                  AND b.startTime > :now
                  AND (:hasStartDate = false OR b.startTime >= :startDateTime)
                  AND (:hasEndDate = false OR b.startTime < :endDateTime)
            ),
            (
                SELECT COUNT(b)
                FROM Booking b
                WHERE b.coach = c
                  AND b.paymentStatus NOT IN (:pendingStatus, :cancelledStatus)
                  AND b.startTime <= :now
                  AND (:hasStartDate = false OR b.startTime >= :startDateTime)
                  AND (:hasEndDate = false OR b.startTime < :endDateTime)
            ),
            (
                SELECT COUNT(DISTINCT bLost.coachee.id)
                FROM Booking bLost
                WHERE bLost.coach = c
                  AND bLost.paymentStatus = :paidStatus
                  AND (:hasStartDate = false OR bLost.startTime >= :startDateTime)
                  AND (:hasEndDate = false OR bLost.startTime < :endDateTime)
                  AND (
                      SELECT COUNT(bRepeat)
                      FROM Booking bRepeat
                      WHERE bRepeat.coach = c
                        AND bRepeat.coachee = bLost.coachee
                        AND bRepeat.paymentStatus = :paidStatus
                  ) = 1
            )
        )
        FROM Coach c
        ORDER BY c.fullNameEn ASC
    """,
            countQuery = "SELECT COUNT(c) FROM Coach c")
    Page<PortalCoachBookingDashboardResponse> findCoachBookingDashboard(
            @Param("paidStatus") PaymentStatus paidStatus,
            @Param("pendingStatus") PaymentStatus pendingStatus,
            @Param("cancelledStatus") PaymentStatus cancelledStatus,
            @Param("now") OffsetDateTime now,
            @Param("hasStartDate") boolean hasStartDate,
            @Param("hasEndDate") boolean hasEndDate,
            @Param("startDateTime") OffsetDateTime startDateTime,
            @Param("endDateTime") OffsetDateTime endDateTime,
            Pageable pageable
    );
}
