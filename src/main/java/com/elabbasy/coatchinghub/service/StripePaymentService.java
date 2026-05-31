package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.config.StripeProperties;
import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.mapper.BookingMapper;
import com.elabbasy.coatchinghub.model.dto.BookingDto;
import com.elabbasy.coatchinghub.model.entity.Booking;
import com.elabbasy.coatchinghub.model.entity.CoachSlot;
import com.elabbasy.coatchinghub.model.entity.Payment;
import com.elabbasy.coatchinghub.model.enums.MeetingProvider;
import com.elabbasy.coatchinghub.model.enums.NotificationType;
import com.elabbasy.coatchinghub.model.enums.PaymentProvider;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import com.elabbasy.coatchinghub.model.enums.SlotStatus;
import com.elabbasy.coatchinghub.model.response.StripePaymentIntentResponse;
import com.elabbasy.coatchinghub.repository.BookingRepository;
import com.elabbasy.coatchinghub.repository.CoachSlotRepository;
import com.elabbasy.coatchinghub.repository.PaymentRepository;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.StripeObject;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StripePaymentService {

    private static final Set<String> ZERO_DECIMAL_CURRENCIES = Set.of(
            "bif", "clp", "djf", "gnf", "jpy", "kmf", "krw", "mga", "pyg", "rwf", "ugx", "vnd", "vuv", "xaf", "xof", "xpf"
    );

    private final StripeProperties stripeProperties;
    private final BookingRepository bookingRepository;
    private final CoachSlotRepository coachSlotRepository;
    private final PaymentRepository paymentRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;
    private final WherebyMeetingService wherebyMeetingService;

    public StripePaymentIntentResponse createPaymentIntent(Long bookingId, Long coacheeId) {
        validateSecretKey();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.BOOKING_REQUIRED));
        if (!coacheeId.equals(booking.getCoachee().getId())) {
            throw new BusinessException(ErrorMessage.UNAUTHORIZE_TO_PERFORM_ACTION);
        }
        if (!PaymentStatus.PENDING.equals(booking.getPaymentStatus())) {
            throw new BusinessException(ErrorMessage.INVALID_PAYMENT_STATUS);
        }

        BigDecimal amount = resolveAmount(booking);
        String currency = stripeProperties.getCurrency().toLowerCase(Locale.ROOT);
        long amountMinor = toMinorUnits(amount, currency);

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountMinor)
                    .setCurrency(currency)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .putAllMetadata(Map.of(
                            "bookingId", booking.getId().toString(),
                            "coacheeId", booking.getCoachee().getId().toString(),
                            "coachId", booking.getCoach().getId().toString()
                    ))
                    .build();

            RequestOptions requestOptions = RequestOptions.builder()
                    .setApiKey(stripeProperties.getSecretKey())
                    .setIdempotencyKey("booking-" + booking.getId() + "-amount-" + amountMinor + "-" + currency)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params, requestOptions);
            Payment payment = paymentRepository
                    .findByProviderAndProviderPaymentIntentId(PaymentProvider.STRIPE, paymentIntent.getId())
                    .orElseGet(Payment::new);

            payment.setBooking(booking);
            payment.setProvider(PaymentProvider.STRIPE);
            payment.setStatus(PaymentStatus.PENDING);
            payment.setAmount(amount);
            payment.setCurrency(currency);
            payment.setProviderPaymentIntentId(paymentIntent.getId());
            Payment savedPayment = paymentRepository.save(payment);

            return StripePaymentIntentResponse.builder()
                    .bookingId(booking.getId())
                    .paymentId(savedPayment.getId())
                    .paymentIntentId(paymentIntent.getId())
                    .clientSecret(paymentIntent.getClientSecret())
                    .amount(amount)
                    .amountMinor(amountMinor)
                    .currency(currency)
                    .status(savedPayment.getStatus())
                    .build();
        } catch (StripeException ex) {
            log.error("Failed to create Stripe payment intent for booking {}", bookingId, ex);
            throw new BusinessException(ErrorMessage.STRIPE_PAYMENT_OPERATION_FAILED);
        }
    }

    public void handleWebhook(String payload, String stripeSignature) {
        validateWebhookSecret();

        Event event;
        try {
            event = Webhook.constructEvent(payload, stripeSignature, stripeProperties.getWebhookSecret());
        } catch (SignatureVerificationException ex) {
            throw new BusinessException(ErrorMessage.UNAUTHORIZE_TO_PERFORM_ACTION);
        }

        if (paymentRepository.existsByProviderAndProviderEventId(PaymentProvider.STRIPE, event.getId())) {
            return;
        }

        StripeObject stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
        if (stripeObject == null) {
            log.warn("Stripe webhook payload could not be deserialized. eventId={}, type={}", event.getId(), event.getType());
            return;
        }

        switch (event.getType()) {
            case "payment_intent.succeeded" -> handlePaymentIntentSucceeded((PaymentIntent) stripeObject, event.getId());
            case "payment_intent.payment_failed" -> handlePaymentIntentFailed((PaymentIntent) stripeObject, event.getId());
            case "charge.refunded" -> handleChargeRefunded((Charge) stripeObject, event.getId());
            case "refund.updated" -> handleRefundUpdated((Refund) stripeObject, event.getId());
            default -> log.debug("Ignoring Stripe webhook event type {}", event.getType());
        }
    }

    public BookingDto refundBookingByAdmin(Long bookingId) {
        validateSecretKey();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.BOOKING_REQUIRED));
        if (!PaymentStatus.PAID.equals(booking.getPaymentStatus())) {
            throw new BusinessException(ErrorMessage.INVALID_PAYMENT_STATUS);
        }

        Payment payment = paymentRepository.findFirstByBookingIdAndStatusOrderByCreatedDateDesc(bookingId, PaymentStatus.PAID)
                .orElseThrow(() -> new BusinessException(ErrorMessage.PAYMENT_NOT_FOUND));
        if (!StringUtils.hasText(payment.getProviderPaymentIntentId())) {
            throw new BusinessException(ErrorMessage.PAYMENT_NOT_FOUND);
        }

        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(payment.getProviderPaymentIntentId())
                    .build();
            Refund refund = Refund.create(params, requestOptions());

            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setProviderRefundId(refund.getId());
            payment.setRefundedAt(OffsetDateTime.now());
            paymentRepository.save(payment);

            booking.setPaymentStatus(PaymentStatus.REFUNDED);
            Booking savedBooking = bookingRepository.save(booking);
            return bookingMapper.toDto(savedBooking);
        } catch (StripeException ex) {
            log.error("Failed to refund Stripe payment for booking {}", bookingId, ex);
            throw new BusinessException(ErrorMessage.STRIPE_PAYMENT_OPERATION_FAILED);
        }
    }

    private void handlePaymentIntentSucceeded(PaymentIntent paymentIntent, String eventId) {
        Payment payment = findPaymentByIntent(paymentIntent.getId());
        Booking booking = payment.getBooking();

        payment.setStatus(PaymentStatus.PAID);
        payment.setProviderEventId(eventId);
        payment.setProviderChargeId(paymentIntent.getLatestCharge());
        payment.setPaidAt(OffsetDateTime.now());
        paymentRepository.save(payment);

        confirmBookingPayment(booking, paymentIntent.getId());
    }

    private void handlePaymentIntentFailed(PaymentIntent paymentIntent, String eventId) {
        Payment payment = findPaymentByIntent(paymentIntent.getId());
        payment.setStatus(PaymentStatus.FAILED);
        payment.setProviderEventId(eventId);
        if (paymentIntent.getLastPaymentError() != null) {
            payment.setFailureReason(paymentIntent.getLastPaymentError().getMessage());
        }
        paymentRepository.save(payment);

        Booking booking = payment.getBooking();
        if (PaymentStatus.PENDING.equals(booking.getPaymentStatus())) {
            booking.setPaymentStatus(PaymentStatus.FAILED);
            bookingRepository.save(booking);
        }
    }

    private void handleChargeRefunded(Charge charge, String eventId) {
        Payment payment = findPaymentByIntent(charge.getPaymentIntent());
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setProviderEventId(eventId);
        payment.setProviderChargeId(charge.getId());
        payment.setRefundedAt(OffsetDateTime.now());
        paymentRepository.save(payment);

        markBookingRefunded(payment.getBooking());
    }

    private void handleRefundUpdated(Refund refund, String eventId) {
        if (!StringUtils.hasText(refund.getPaymentIntent())) {
            return;
        }

        Payment payment = findPaymentByIntent(refund.getPaymentIntent());
        payment.setProviderEventId(eventId);
        payment.setProviderRefundId(refund.getId());
        if ("succeeded".equals(refund.getStatus())) {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setRefundedAt(OffsetDateTime.now());
            markBookingRefunded(payment.getBooking());
        }
        paymentRepository.save(payment);
    }

    private void confirmBookingPayment(Booking booking, String transactionId) {
        if (PaymentStatus.PAID.equals(booking.getPaymentStatus())) {
            return;
        }
        if (!PaymentStatus.PENDING.equals(booking.getPaymentStatus()) && !PaymentStatus.FAILED.equals(booking.getPaymentStatus())) {
            throw new BusinessException(ErrorMessage.INVALID_PAYMENT_STATUS);
        }

        booking.setPaymentDateTime(OffsetDateTime.now());
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setPaymentTransaction(transactionId);
        createMeetingIfNeeded(booking);
        Booking savedBooking = bookingRepository.save(booking);

        CoachSlot coachSlot = savedBooking.getCoachSlot();
        if (coachSlot == null) {
            throw new BusinessException(ErrorMessage.SLOT_NOT_FOUND);
        }
        if (!SlotStatus.BOOKED.equals(coachSlot.getStatus())) {
            coachSlot.setStatus(SlotStatus.BOOKED);
            coachSlotRepository.save(coachSlot);
        }

        notificationService.notifyBoth(savedBooking.getCoach().getId(),
                savedBooking.getCoachee().getId(),
                "New Booking",
                "حجز جديد",
                "You have a new booking scheduled",
                "لديك حجز جديد مجدول",
                NotificationType.BOOKING_CREATED,
                savedBooking.getId()
        );
    }

    private void markBookingRefunded(Booking booking) {
        if (!PaymentStatus.REFUNDED.equals(booking.getPaymentStatus())) {
            booking.setPaymentStatus(PaymentStatus.REFUNDED);
            bookingRepository.save(booking);
        }
    }

    private Payment findPaymentByIntent(String paymentIntentId) {
        if (!StringUtils.hasText(paymentIntentId)) {
            throw new BusinessException(ErrorMessage.PAYMENT_NOT_FOUND);
        }
        return paymentRepository.findByProviderAndProviderPaymentIntentId(PaymentProvider.STRIPE, paymentIntentId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.PAYMENT_NOT_FOUND));
    }

    private void createMeetingIfNeeded(Booking booking) {
        if (!PaymentStatus.PAID.equals(booking.getPaymentStatus())) {
            return;
        }
        if (StringUtils.hasText(booking.getMeetingId()) && booking.getMeetingDeletedAt() == null) {
            return;
        }

        WherebyMeetingService.WherebyMeetingResult meeting = wherebyMeetingService.createMeeting(booking.getId(), booking.getEndTime());
        booking.setMeetingProvider(MeetingProvider.WHEREBY);
        booking.setMeetingId(meeting.meetingId());
        booking.setMeetingRoomUrl(meeting.roomUrl());
        booking.setMeetingHostRoomUrl(meeting.hostRoomUrl());
        booking.setMeetingCreatedAt(OffsetDateTime.now());
        booking.setMeetingDeletedAt(null);
    }

    private BigDecimal resolveAmount(Booking booking) {
        Double finalPrice = Objects.requireNonNullElse(booking.getFinalPrice(), booking.getPrice());
        if (finalPrice == null || finalPrice <= 0) {
            throw new BusinessException(ErrorMessage.INVALID_PAYMENT_STATUS);
        }
        return BigDecimal.valueOf(finalPrice).setScale(2, RoundingMode.HALF_UP);
    }

    private long toMinorUnits(BigDecimal amount, String currency) {
        if (ZERO_DECIMAL_CURRENCIES.contains(currency)) {
            return amount.setScale(0, RoundingMode.HALF_UP).longValueExact();
        }
        return amount.multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    private RequestOptions requestOptions() {
        return RequestOptions.builder()
                .setApiKey(stripeProperties.getSecretKey())
                .build();
    }

    private void validateSecretKey() {
        if (!StringUtils.hasText(stripeProperties.getSecretKey())) {
            throw new BusinessException(ErrorMessage.STRIPE_SECRET_KEY_REQUIRED);
        }
    }

    private void validateWebhookSecret() {
        if (!StringUtils.hasText(stripeProperties.getWebhookSecret())) {
            throw new BusinessException(ErrorMessage.STRIPE_WEBHOOK_SECRET_REQUIRED);
        }
    }
}
