package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Booking;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import com.elabbasy.coatchinghub.model.response.CoachBookingProjection;
import com.elabbasy.coatchinghub.model.response.CoacheeBookingProjection;
import com.elabbasy.coatchinghub.model.response.CoacheeCoachBookingProjection;
import com.elabbasy.coatchinghub.model.response.PortalIndustryPaidBookingCountResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    @Query("""
        SELECT COUNT(b) > 0
        FROM Booking b
        WHERE b.coach.id = :coachId
          AND b.startTime < :newEndTime
          AND b.endTime > :newStartTime
    """)
    boolean existsOverlappingBooking(
            @Param("coachId") Long coachId,
            @Param("newStartTime") OffsetDateTime newStartTime,
            @Param("newEndTime") OffsetDateTime newEndTime
    );

    Integer countByCouponId(Long id);

    Integer countByCoachIdAndPaymentStatus(Long coachId, PaymentStatus paymentStatus);

    Long countByPaymentStatus(PaymentStatus paymentStatus);

    Long countByStartTimeGreaterThanEqual(OffsetDateTime startTime);

    Long countByStartTimeGreaterThanEqualAndStartTimeLessThan(OffsetDateTime startTime, OffsetDateTime endTime);

    @Query("""
        SELECT COALESCE(SUM(b.finalPrice), 0)
        FROM Booking b
        WHERE b.paymentStatus = :paymentStatus
    """)
    Double sumFinalPriceByPaymentStatus(@Param("paymentStatus") PaymentStatus paymentStatus);

    @Query("""
        SELECT COALESCE(SUM(b.finalPrice), 0)
        FROM Booking b
        WHERE b.paymentStatus = :paymentStatus
          AND b.startTime >= :startTime
          AND b.startTime < :endTime
    """)
    Double sumFinalPriceByPaymentStatusAndStartTimeBetween(
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime
    );

    @Query("""
        SELECT COALESCE(SUM(b.finalPrice), 0)
        FROM Booking b
        WHERE b.paymentStatus = :paymentStatus
          AND b.startTime >= :startTime
    """)
    Double sumFinalPriceByPaymentStatusAndStartTimeGreaterThanEqual(
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("startTime") OffsetDateTime startTime
    );

    @Query("""
        SELECT new com.elabbasy.coatchinghub.model.response.PortalIndustryPaidBookingCountResponse(
            industry.id,
            industry.nameEn,
            industry.nameAr,
            COUNT(b)
        )
        FROM Booking b
        JOIN b.coach coach
        JOIN coach.coachingIndustries industry
        WHERE b.paymentStatus = :paymentStatus
        GROUP BY industry.id, industry.nameEn, industry.nameAr
        ORDER BY COUNT(b) DESC, industry.nameEn ASC
    """)
    java.util.List<PortalIndustryPaidBookingCountResponse> countPaidBookingsByCoachingIndustry(
            @Param("paymentStatus") PaymentStatus paymentStatus
    );

    Page<CoacheeCoachBookingProjection> findByCoachIdAndCoacheeIdAndPaymentStatus(Long coachId, Long coacheeId, PaymentStatus paymentStatus, Pageable pageable);

    Page<CoacheeCoachBookingProjection>
    findByCoachIdAndCoacheeIdAndPaymentStatusAndEndTimeGreaterThanEqual(
            Long coachId,
            Long coacheeId,
            PaymentStatus paymentStatus,
            OffsetDateTime now,
            Pageable pageable
    );

    Page<CoacheeCoachBookingProjection>
    findByCoachIdAndCoacheeIdAndPaymentStatusAndEndTimeLessThan(
            Long coachId,
            Long coacheeId,
            PaymentStatus paymentStatus,
            OffsetDateTime now,
            Pageable pageable
    );

    Page<CoachBookingProjection>
    findByCoachIdAndPaymentStatusAndEndTimeGreaterThanEqual(
            Long coachId,
            PaymentStatus paymentStatus,
            OffsetDateTime now,
            Pageable pageable
    );

    Page<CoachBookingProjection>
    findByCoachIdAndPaymentStatusAndEndTimeLessThan(
            Long coachId,
            PaymentStatus paymentStatus,
            OffsetDateTime now,
            Pageable pageable
    );

    @Query("""
    SELECT b.id as id,
           b.startTime as startTime,
           b.endTime as endTime,
           b.periodMinutes as periodMinutes,
           b.price as price,
           b.discount as discount,
           b.finalPrice as finalPrice,
           b.paymentStatus as paymentStatus,
           coachee.fullName as coacheeFullName,
           coachee.profileImageUrl as coacheeProfileImageUrl
    FROM Booking b
    JOIN b.coachee coachee
    WHERE b.coach.id = :coachId
      AND (
          (b.paymentStatus = :paidStatus AND b.endTime < :now)
          OR b.paymentStatus = :cancelledStatus
      )
""")
    Page<CoachBookingProjection> findPastBookingsForCoachIncludingCancelled(
            @Param("coachId") Long coachId,
            @Param("paidStatus") PaymentStatus paidStatus,
            @Param("cancelledStatus") PaymentStatus cancelledStatus,
            @Param("now") OffsetDateTime now,
            Pageable pageable
    );

    @Query("""
    SELECT b.id as id,
           b.startTime as startTime,
           b.endTime as endTime,
           b.periodMinutes as periodMinutes,
           b.price as price,
           b.discount as discount,
           b.finalPrice as finalPrice,
           c.id as coachId,
           c.fullNameEn as coachFullNameEn,
           c.fullNameAr as coachFullNameAr,
           c.profileImageUrl as coachProfileImageUrl
    FROM Booking b
    JOIN b.coach c
    WHERE b.coachee.id = :coacheeId
      AND b.paymentStatus = :paymentStatus
      AND b.endTime >= :now
""")
    Page<CoacheeBookingProjection> findByCoacheeIdAndPaymentStatusAndEndTimeGreaterThanEqual(
            Long coacheeId,
            PaymentStatus paymentStatus,
            OffsetDateTime now,
            Pageable pageable
    );

    @Query("""
    SELECT b.id as id,
           b.startTime as startTime,
           b.endTime as endTime,
           b.periodMinutes as periodMinutes,
           b.price as price,
           b.discount as discount,
           b.finalPrice as finalPrice,
           c.id as coachId,
           c.fullNameEn as coachFullNameEn,
           c.fullNameAr as coachFullNameAr,
           c.profileImageUrl as coachProfileImageUrl
    FROM Booking b
    JOIN b.coach c
    WHERE b.coachee.id = :coacheeId
      AND b.paymentStatus = :paymentStatus
      AND b.endTime < :now
""")
    Page<CoacheeBookingProjection>
    findByCoacheeIdAndPaymentStatusAndEndTimeLessThan(
            Long coacheeId,
            PaymentStatus paymentStatus,
            OffsetDateTime now,
            Pageable pageable
    );

    @Query("""
    SELECT b.id as id,
           b.startTime as startTime,
           b.endTime as endTime,
           b.periodMinutes as periodMinutes,
           b.price as price,
           b.discount as discount,
           b.finalPrice as finalPrice,
           b.paymentStatus as paymentStatus,
           c.id as coachId,
           c.fullNameEn as coachFullNameEn,
           c.fullNameAr as coachFullNameAr,
           c.profileImageUrl as coachProfileImageUrl
    FROM Booking b
    JOIN b.coach c
    WHERE b.coachee.id = :coacheeId
      AND (
          (b.paymentStatus = :paidStatus AND b.endTime < :now)
          OR b.paymentStatus = :cancelledStatus
      )
""")
    Page<CoacheeBookingProjection> findPastBookingsForCoacheeIncludingCancelled(
            @Param("coacheeId") Long coacheeId,
            @Param("paidStatus") PaymentStatus paidStatus,
            @Param("cancelledStatus") PaymentStatus cancelledStatus,
            @Param("now") OffsetDateTime now,
            Pageable pageable
    );

}
