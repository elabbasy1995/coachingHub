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

import java.time.OffsetDateTime;
import java.util.List;


@Repository
public interface CoachRepository extends JpaRepository<Coach, Long>, CustomCoachRepository {

    Long countByStatus(CoachStatus status);

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
            ),
            (
                SELECT COALESCE(SUM(b.finalPrice), 0)
                FROM Booking b
                WHERE b.coach = c
                  AND b.paymentStatus = :paidStatus
            ),
            (
                SELECT COUNT(b)
                FROM Booking b
                WHERE b.coach = c
                  AND b.paymentStatus = :cancelledStatus
            ),
            (
                SELECT COUNT(b)
                FROM Booking b
                WHERE b.coach = c
                  AND b.paymentStatus NOT IN (:pendingStatus, :cancelledStatus)
                  AND b.startTime > :now
            ),
            (
                SELECT COUNT(b)
                FROM Booking b
                WHERE b.coach = c
                  AND b.paymentStatus NOT IN (:pendingStatus, :cancelledStatus)
                  AND b.startTime <= :now
            ),
            (
                SELECT COUNT(DISTINCT bLost.coachee.id)
                FROM Booking bLost
                WHERE bLost.coach = c
                  AND bLost.paymentStatus = :paidStatus
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
            Pageable pageable
    );
}
