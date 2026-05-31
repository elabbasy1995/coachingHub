package com.elabbasy.coatchinghub.repository;

import com.elabbasy.coatchinghub.model.entity.Payment;
import com.elabbasy.coatchinghub.model.enums.PaymentProvider;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import com.elabbasy.coatchinghub.model.response.PortalPaymentTransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByProviderAndProviderPaymentIntentId(PaymentProvider provider, String providerPaymentIntentId);

    Optional<Payment> findByProviderAndProviderCheckoutSessionId(PaymentProvider provider, String providerCheckoutSessionId);

    boolean existsByProviderAndProviderEventId(PaymentProvider provider, String providerEventId);

    Optional<Payment> findFirstByBookingIdAndStatusOrderByCreatedDateDesc(Long bookingId, PaymentStatus status);

    @Query(value = """
            SELECT new com.elabbasy.coatchinghub.model.response.PortalPaymentTransactionResponse(
                p.id,
                b.id,
                p.provider,
                p.status,
                p.amount,
                p.currency,
                p.providerPaymentIntentId,
                p.providerCheckoutSessionId,
                p.providerChargeId,
                p.providerRefundId,
                p.providerEventId,
                p.failureReason,
                p.paidAt,
                p.refundedAt,
                p.createdDate,
                p.updatedDate,
                b.paymentDateTime,
                b.paymentTransaction,
                coach.id,
                coach.fullNameEn,
                coach.fullNameAr,
                coach.email,
                coachee.id,
                coachee.fullName,
                coacheeUser.email,
                coachee.phoneNumber
            )
            FROM Payment p
            JOIN p.booking b
            JOIN b.coach coach
            JOIN b.coachee coachee
            JOIN coachee.user coacheeUser
            WHERE (:search IS NULL
                OR LOWER(coach.fullNameEn) LIKE CONCAT('%', :search, '%')
                OR LOWER(coach.fullNameAr) LIKE CONCAT('%', :search, '%')
                OR LOWER(coach.email) LIKE CONCAT('%', :search, '%')
                OR LOWER(coachee.fullName) LIKE CONCAT('%', :search, '%')
                OR LOWER(coacheeUser.email) LIKE CONCAT('%', :search, '%')
                OR LOWER(coachee.phoneNumber) LIKE CONCAT('%', :search, '%'))
              AND (:bookingId IS NULL OR b.id = :bookingId)
              AND (:provider IS NULL OR p.provider = :provider)
              AND (:status IS NULL OR p.status = :status)
              AND (:transaction IS NULL
                OR p.providerPaymentIntentId = :transaction
                OR p.providerCheckoutSessionId = :transaction
                OR p.providerChargeId = :transaction
                OR p.providerRefundId = :transaction
                OR b.paymentTransaction = :transaction)
              AND (:startDate IS NULL OR p.createdDate >= :startDate)
              AND (:endDate IS NULL OR p.createdDate < :endDate)
            """,
            countQuery = """
            SELECT COUNT(p)
            FROM Payment p
            JOIN p.booking b
            JOIN b.coach coach
            JOIN b.coachee coachee
            JOIN coachee.user coacheeUser
            WHERE (:search IS NULL
                OR LOWER(coach.fullNameEn) LIKE CONCAT('%', :search, '%')
                OR LOWER(coach.fullNameAr) LIKE CONCAT('%', :search, '%')
                OR LOWER(coach.email) LIKE CONCAT('%', :search, '%')
                OR LOWER(coachee.fullName) LIKE CONCAT('%', :search, '%')
                OR LOWER(coacheeUser.email) LIKE CONCAT('%', :search, '%')
                OR LOWER(coachee.phoneNumber) LIKE CONCAT('%', :search, '%'))
              AND (:bookingId IS NULL OR b.id = :bookingId)
              AND (:provider IS NULL OR p.provider = :provider)
              AND (:status IS NULL OR p.status = :status)
              AND (:transaction IS NULL
                OR p.providerPaymentIntentId = :transaction
                OR p.providerCheckoutSessionId = :transaction
                OR p.providerChargeId = :transaction
                OR p.providerRefundId = :transaction
                OR b.paymentTransaction = :transaction)
              AND (:startDate IS NULL OR p.createdDate >= :startDate)
              AND (:endDate IS NULL OR p.createdDate < :endDate)
            """)
    Page<PortalPaymentTransactionResponse> findTransactionsForPortal(
            @Param("search") String search,
            @Param("bookingId") Long bookingId,
            @Param("provider") PaymentProvider provider,
            @Param("status") PaymentStatus status,
            @Param("transaction") String transaction,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
