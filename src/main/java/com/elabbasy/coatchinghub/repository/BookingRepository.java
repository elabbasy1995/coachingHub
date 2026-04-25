package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Booking;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import com.elabbasy.coatchinghub.model.response.CoachBookingProjection;
import com.elabbasy.coatchinghub.model.response.CoacheeBookingProjection;
import com.elabbasy.coatchinghub.model.response.CoacheeCoachBookingProjection;
import com.elabbasy.coatchinghub.model.response.PortalBookingListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

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
    SELECT new com.elabbasy.coatchinghub.model.response.PortalBookingListResponse(
           b.id,
           b.startTime,
           b.endTime,
           b.periodMinutes,
           b.slotType,
           b.price,
           b.discount,
           b.finalPrice,
           b.paymentStatus,
           b.paymentDateTime,
           b.paymentTransaction,
           coach.fullNameEn,
           coach.fullNameAr,
           coachUser.email,
           coachee.fullName,
           coacheeUser.email
    )
    FROM Booking b
    JOIN b.coach coach
    JOIN coach.user coachUser
    JOIN b.coachee coachee
    JOIN coachee.user coacheeUser
    WHERE (
        lower(coach.fullNameEn) LIKE lower(concat('%', :search, '%'))
        OR lower(coach.fullNameAr) LIKE lower(concat('%', :search, '%'))
        OR lower(coachUser.email) LIKE lower(concat('%', :search, '%'))
        OR lower(coachee.fullName) LIKE lower(concat('%', :search, '%'))
        OR lower(coacheeUser.email) LIKE lower(concat('%', :search, '%'))
        OR lower(coalesce(b.paymentTransaction, '')) LIKE lower(concat('%', :search, '%'))
    )
""")
    Page<PortalBookingListResponse> searchPortalBookings(@Param("search") String search, Pageable pageable);

    @Query("""
    SELECT new com.elabbasy.coatchinghub.model.response.PortalBookingListResponse(
           b.id,
           b.startTime,
           b.endTime,
           b.periodMinutes,
           b.slotType,
           b.price,
           b.discount,
           b.finalPrice,
           b.paymentStatus,
           b.paymentDateTime,
           b.paymentTransaction,
           coach.fullNameEn,
           coach.fullNameAr,
           coachUser.email,
           coachee.fullName,
           coacheeUser.email
    )
    FROM Booking b
    JOIN b.coach coach
    JOIN coach.user coachUser
    JOIN b.coachee coachee
    JOIN coachee.user coacheeUser
""")
    Page<PortalBookingListResponse> findAllPortalBookings(Pageable pageable);

}
