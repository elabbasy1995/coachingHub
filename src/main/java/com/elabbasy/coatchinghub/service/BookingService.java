package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.constant.ErrorMessage;
import com.elabbasy.coatchinghub.exception.BusinessException;
import com.elabbasy.coatchinghub.mapper.BookingMapper;
import com.elabbasy.coatchinghub.model.dto.BookingDto;
import com.elabbasy.coatchinghub.model.entity.*;
import com.elabbasy.coatchinghub.model.enums.*;
import com.elabbasy.coatchinghub.model.request.ApiRequest;
import com.elabbasy.coatchinghub.model.request.ApplyCouponRequest;
import com.elabbasy.coatchinghub.model.request.CreateBookingRequest;
import com.elabbasy.coatchinghub.model.request.PayBookingRequest;
import com.elabbasy.coatchinghub.model.request.RescheduleBookingRequest;
import com.elabbasy.coatchinghub.model.response.*;
import com.elabbasy.coatchinghub.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CoachRepository coachRepository;
    private final CoacheeRepository coacheeRepository;
    private final CoachSlotRepository coachSlotRepository;
    private final CouponRepository couponRepository;
    private final NotificationService notificationService;
    private final CoachingIndustryRepository coachingIndustryRepository;

    public BookingDto createBooking(CreateBookingRequest request, Long coacheeId) {

        Coach coach = coachRepository.findById(request.getCoachId())
                .orElseThrow(() -> new BusinessException(ErrorMessage.COACH_NOT_FOUND));
        if (!CoachStatus.APPROVED.equals(coach.getStatus()))
            throw new BusinessException(ErrorMessage.COACH_NOT_FOUND);

        Coachee coachee = coacheeRepository.findById(coacheeId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.COACHEE_NOT_FOUND));

        CoachSlot coachSlot = coachSlotRepository.findById(request.getCoachSlotId()).orElseThrow(() -> new BusinessException(ErrorMessage.SLOT_NOT_FOUND));
        if (!SlotStatus.AVAILABLE.equals(coachSlot.getStatus()))
            throw new BusinessException(ErrorMessage.CANNOT_REMOVE_THIS_SLOT_IT_IS_ALREAY_USED);

        OffsetDateTime startTime = coachSlot.getStartTimeUtc();
        Integer periodMinutes = coachSlot.getPeriodMinutes();
        OffsetDateTime endTime = startTime.plusMinutes(periodMinutes);

        // Prevent booking in the past
        if (startTime.isBefore(OffsetDateTime.now())) {
            throw new BusinessException(ErrorMessage.CAN_NOT_RESERVE_SLOT_IN_THE_PAST);
        }

        // Overlap check
        boolean overlap = bookingRepository.existsOverlappingBooking(
                coach.getId(),
                startTime,
                endTime
        );

        if (overlap) {
            throw new BusinessException(ErrorMessage.COACH_NOT_AVAILABLE_AT_THIS_TIME);
        }

        Booking booking = new Booking();
        booking.setCoach(coach);
        booking.setCoachee(coachee);
        booking.setStartTime(startTime);
        booking.setEndTime(endTime);
        booking.setPeriodMinutes(periodMinutes);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setCoachSlot(coachSlot);
        booking.setSlotType(coachSlot.getSlotType());
        if (SlotType.HALF_HOUR.equals(coachSlot.getSlotType())) {
            booking.setPrice(Objects.nonNull(coach.getHalfHourPrice()) ? coach.getHalfHourPrice() : 0.0);
            booking.setFinalPrice(Objects.nonNull(coach.getHalfHourPrice()) ? coach.getHalfHourPrice() : 0.0);
        } else if (SlotType.HOUR.equals(coachSlot.getSlotType())) {
            booking.setPrice(Objects.nonNull(coach.getHourlyPrice()) ? coach.getHourlyPrice() : 0.0);
            booking.setFinalPrice(Objects.nonNull(coach.getHourlyPrice()) ? coach.getHourlyPrice() : 0.0);
        } else if (SlotType.ONE_AND_HALF_HOUR.equals(coachSlot.getSlotType())) {
            booking.setPrice(Objects.nonNull(coach.getOneAndHalfHourPrice()) ? coach.getOneAndHalfHourPrice() : 0.0);
            booking.setFinalPrice(Objects.nonNull(coach.getOneAndHalfHourPrice()) ? coach.getOneAndHalfHourPrice() : 0.0);
        } else if (SlotType.TWO_HOURS.equals(coachSlot.getSlotType())) {
            booking.setPrice(Objects.nonNull(coach.getTwoHoursPrice()) ? coach.getTwoHoursPrice() : 0.0);
            booking.setFinalPrice(Objects.nonNull(coach.getTwoHoursPrice()) ? coach.getTwoHoursPrice() : 0.0);
        }

        Booking saved = bookingRepository.save(booking);


        BookingDto dto = bookingMapper.toDto(saved);
        if (Objects.nonNull(dto) && Objects.nonNull(dto.getCoach())) {
            Integer countByCoachIdAndPaymentStatus = bookingRepository.countByCoachIdAndPaymentStatus(coach.getId(), PaymentStatus.PAID);
            dto.getCoach().setBookingCount(countByCoachIdAndPaymentStatus);
        }
        return dto;
    }

    public BookingDto applyCoupon(ApplyCouponRequest applyCouponRequest, Long coacheeId) {
        Booking booking = bookingRepository.findById(applyCouponRequest.getBookingId()).orElseThrow(() -> new BusinessException(ErrorMessage.BOOKING_REQUIRED));
        if (!coacheeId.equals(booking.getCoachee().getId())) {
            throw new BusinessException(ErrorMessage.UNAUTHORIZE_TO_PERFORM_ACTION);
        }
        Coupon coupon = couponRepository.findByCode(applyCouponRequest.getCode()).orElseThrow(() -> new BusinessException(ErrorMessage.INVALID_COUPON_CODE));
        if (coupon.getSoftDelete()) {
            throw new BusinessException(ErrorMessage.INVALID_COUPON_CODE);
        }
        if (LocalDate.now().isAfter(coupon.getExpiryDate())) {
            throw new BusinessException(ErrorMessage.CODE_IS_EXPIRED);
        }
        Integer count = bookingRepository.countByCouponId(coupon.getId());
        if (!coupon.getUnlimitedUsage() && count >= coupon.getTimesOfUse()) {
            throw new BusinessException(ErrorMessage.INVALID_COUPON_CODE);
        }
        if (!coupon.getAllCoaches()) {
            if ((Objects.isNull(coupon.getCoaches()) || coupon.getCoaches().isEmpty())) {
                throw new BusinessException(ErrorMessage.INVALID_COUPON_CODE);
            }
            if (!coupon.getCoaches().stream().map(Coach::getId).toList().contains(booking.getCoach().getId())) {
                throw new BusinessException(ErrorMessage.INVALID_COUPON_CODE);
            }
        }
        booking.setDiscount((coupon.getDiscountPercentage() * booking.getPrice()) / 100);
        booking.setFinalPrice(booking.getPrice() - booking.getDiscount());
        booking.setCoupon(coupon);
        Booking save = bookingRepository.save(booking);

        return bookingMapper.toDto(save);
    }

    public BookingDto deleteCoupon(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorMessage.BOOKING_REQUIRED));
        if (!PaymentStatus.PAID.equals(booking.getPaymentStatus())) {
            throw new BusinessException(ErrorMessage.INVALID_PAYMENT_STATUS);
        }

        booking.setDiscount(null);
        booking.setFinalPrice(booking.getPrice());

        Booking save = bookingRepository.save(booking);

        return bookingMapper.toDto(save);
    }

    public BookingDto payBooking(PayBookingRequest bookingRequest, Long coacheeId) {
        Booking booking = bookingRepository.findById(bookingRequest.getBookingId()).orElseThrow(() -> new BusinessException(ErrorMessage.BOOKING_REQUIRED));
        if (!coacheeId.equals(booking.getCoachee().getId())) {
            throw new BusinessException(ErrorMessage.UNAUTHORIZE_TO_PERFORM_ACTION);
        }

        booking.setPaymentDateTime(OffsetDateTime.now());
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setPaymentTransaction(bookingRequest.getTransactionId());

        Booking save = bookingRepository.save(booking);

        CoachSlot coachSlot = coachSlotRepository.findById(booking.getCoachSlot().getId()).orElseThrow(() -> new BusinessException(ErrorMessage.SLOT_NOT_FOUND));

        coachSlot.setStatus(SlotStatus.BOOKED);
        coachSlotRepository.save(coachSlot);

        notificationService.notifyBoth(booking.getCoach().getId(),
                booking.getCoachee().getId(),
                "New Booking",
                "You have a new booking scheduled",
                NotificationType.BOOKING_CREATED,
                booking.getId()
                );

        return bookingMapper.toDto(save);
    }

    public BookingDto rescheduleBooking(RescheduleBookingRequest request, Long coacheeId) {
        return rescheduleBookingInternal(request, coacheeId, false);
    }

    public BookingDto rescheduleBookingByAdmin(RescheduleBookingRequest request) {
        return rescheduleBookingInternal(request, null, true);
    }

    public BookingDto cancelBookingByAdmin(Long bookingId) {
        Booking booking = findBookingForAdminAction(bookingId);

        if (!PaymentStatus.PENDING.equals(booking.getPaymentStatus())) {
            throw new BusinessException(ErrorMessage.INVALID_PAYMENT_STATUS);
        }

        validateBookingNotStarted(booking);
        releaseBookingSlot(booking);
        booking.setPaymentStatus(PaymentStatus.CANCELLED);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    public BookingDto refundBookingByAdmin(Long bookingId) {
        Booking booking = findBookingForAdminAction(bookingId);

        if (!PaymentStatus.PAID.equals(booking.getPaymentStatus())) {
            throw new BusinessException(ErrorMessage.INVALID_PAYMENT_STATUS);
        }

        validateBookingNotStarted(booking);
        releaseBookingSlot(booking);
        booking.setPaymentStatus(PaymentStatus.REFUNDED);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    private BookingDto rescheduleBookingInternal(RescheduleBookingRequest request, Long coacheeId, boolean adminAction) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BusinessException(ErrorMessage.BOOKING_REQUIRED));

        if (!adminAction && !coacheeId.equals(booking.getCoachee().getId())) {
            throw new BusinessException(ErrorMessage.UNAUTHORIZE_TO_PERFORM_ACTION);
        }

        OffsetDateTime now = OffsetDateTime.now();
        if (!now.isBefore(booking.getStartTime())) {
            throw new BusinessException(ErrorMessage.BOOKING_ALREADY_STARTED);
        }

        long minutesUntilStart = ChronoUnit.MINUTES.between(now, booking.getStartTime());
        if (minutesUntilStart <= 15) {
            throw new BusinessException(ErrorMessage.RESCHEDULE_NOT_ALLOWED_WITHIN_15_MINUTES);
        }

        CoachSlot newSlot = coachSlotRepository.findById(request.getCoachSlotId())
                .orElseThrow(() -> new BusinessException(ErrorMessage.SLOT_NOT_FOUND));

        if (!SlotStatus.AVAILABLE.equals(newSlot.getStatus())) {
            throw new BusinessException(ErrorMessage.INVALID_RESCHEDULE_SLOT);
        }

        if (!booking.getCoach().getId().equals(newSlot.getCoach().getId())) {
            throw new BusinessException(ErrorMessage.INVALID_RESCHEDULE_SLOT);
        }

        if (!Objects.equals(booking.getPeriodMinutes(), newSlot.getPeriodMinutes())
                || !Objects.equals(booking.getSlotType(), newSlot.getSlotType())) {
            throw new BusinessException(ErrorMessage.INVALID_RESCHEDULE_SLOT);
        }

        if (booking.getCoachSlot() != null && booking.getCoachSlot().getId().equals(newSlot.getId())) {
            throw new BusinessException(ErrorMessage.INVALID_RESCHEDULE_SLOT);
        }

        OffsetDateTime newStartTime = newSlot.getStartTimeUtc();
        OffsetDateTime newEndTime = newSlot.getEndTimeUtc();

        if (!now.isBefore(newStartTime)) {
            throw new BusinessException(ErrorMessage.INVALID_RESCHEDULE_SLOT);
        }

        boolean overlap = bookingRepository.existsOverlappingBooking(
                booking.getCoach().getId(),
                newStartTime,
                newEndTime
        );
        if (overlap) {
            throw new BusinessException(ErrorMessage.COACH_NOT_AVAILABLE_AT_THIS_TIME);
        }

        CoachSlot oldSlot = booking.getCoachSlot();
        if (oldSlot != null) {
            oldSlot.setStatus(SlotStatus.AVAILABLE);
            coachSlotRepository.save(oldSlot);
        }

        newSlot.setStatus(SlotStatus.BOOKED);
        coachSlotRepository.save(newSlot);

        booking.setCoachSlot(newSlot);
        booking.setStartTime(newStartTime);
        booking.setEndTime(newEndTime);
        booking.setPeriodMinutes(newSlot.getPeriodMinutes());
        booking.setSlotType(newSlot.getSlotType());

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    private Booking findBookingForAdminAction(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.BOOKING_REQUIRED));
    }

    private void validateBookingNotStarted(Booking booking) {
        if (!OffsetDateTime.now().isBefore(booking.getStartTime())) {
            throw new BusinessException(ErrorMessage.BOOKING_ALREADY_STARTED);
        }
    }

    private void releaseBookingSlot(Booking booking) {
        CoachSlot coachSlot = booking.getCoachSlot();
        if (coachSlot == null) {
            return;
        }

        if (SlotStatus.BOOKED.equals(coachSlot.getStatus())) {
            coachSlot.setStatus(SlotStatus.AVAILABLE);
            coachSlotRepository.save(coachSlot);
        }
    }

    public ApiResponse<List<CoacheeCoachBookingProjection>> findAllCoacheeBooking(Long coachId, Long coacheeId, Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<CoacheeCoachBookingProjection> page = bookingRepository.findByCoachIdAndCoacheeIdAndPaymentStatus(coachId, coacheeId, PaymentStatus.PAID, pageable);
        if (Objects.nonNull(page)) {
            return new ApiResponse<>(page.getContent(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
        } else
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    public ApiResponse<List<PortalBookingListResponse>> findAllForAdmin(String search,
                                                                        Integer pageIndex,
                                                                        Integer pageSize,
                                                                        String sortBy,
                                                                        String sortDir) {
        String normalizedSearch = search == null || search.trim().isEmpty() ? null : search.trim();

        ApiRequest<Void> apiRequest = ApiRequest.<Void>builder()
                .pageIndex(pageIndex == null ? 0 : pageIndex)
                .pageSize(pageSize == null ? 20 : pageSize)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();

        Page<PortalBookingListResponse> page = normalizedSearch == null
                ? bookingRepository.findAllPortalBookings(apiRequest.buildPagination())
                : bookingRepository.searchPortalBookings(normalizedSearch, apiRequest.buildPagination());

        OffsetDateTime now = OffsetDateTime.now();
        List<PortalBookingListResponse> response = page.getContent()
                .stream()
                .peek(booking -> booking.setBookingStatus(resolvePortalBookingStatus(
                        booking.getStartTime(),
                        booking.getEndTime(),
                        now
                )))
                .toList();

        return new ApiResponse<>(response, page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    public ApiResponse<List<CoacheeCoachBookingProjection>> findUpcomingCoacheeBooking(Long coachId, Long coacheeId, Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<CoacheeCoachBookingProjection> page = bookingRepository.findByCoachIdAndCoacheeIdAndPaymentStatusAndEndTimeGreaterThanEqual(coachId, coacheeId, PaymentStatus.PAID, OffsetDateTime.now(), pageable);
        if (Objects.nonNull(page)) {
            return new ApiResponse<>(page.getContent(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
        } else
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    public ApiResponse<List<CoacheeCoachBookingProjection>> findPastCoacheeBooking(Long coachId, Long coacheeId, Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<CoacheeCoachBookingProjection> page = bookingRepository.findByCoachIdAndCoacheeIdAndPaymentStatusAndEndTimeLessThan(coachId, coacheeId, PaymentStatus.PAID, OffsetDateTime.now(), pageable);
        if (Objects.nonNull(page)) {
            return new ApiResponse<>(page.getContent(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
        } else
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    public ApiResponse<List<CoachBookingResponse>> findUpcomingBookingForCoach(Long coachId, Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<CoachBookingProjection> page = bookingRepository.findByCoachIdAndPaymentStatusAndEndTimeGreaterThanEqual(coachId, PaymentStatus.PAID, OffsetDateTime.now(), pageable);
        if (Objects.nonNull(page)) {
            OffsetDateTime now = OffsetDateTime.now();

            List<CoachBookingResponse> response = page.getContent()
                    .stream()
                    .map(b -> {

                        BookingStatus status = resolveStatus(
                                b.getStartTime(),
                                b.getEndTime(),
                                now
                        );

                        return CoachBookingResponse.builder()
                                .id(b.getId())
                                .startTime(b.getStartTime())
                                .endTime(b.getEndTime())
                                .periodMinutes(b.getPeriodMinutes())
                                .price(b.getPrice())
                                .discount(b.getDiscount())
                                .finalPrice(b.getFinalPrice())
                                .coacheeFullName(b.getCoacheeFullName())
                                .coacheeProfileImageUrl(b.getCoacheeProfileImageUrl())
                                .status(status)
                                .statusWrapper(status != null ? StatusWrapper.builder().nameAr(status.getNameAr()).nameEn(status.getNameEn()).build() : null)
                                // actions
                                .actions(resolveActions(
                                        b.getStartTime(),
                                        b.getEndTime(),
                                        now
                                ))
                                .build();
                    })
                    .toList();

            return new ApiResponse<>(response, page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
        } else
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    public ApiResponse<List<CoachBookingResponse>> findPastBookingForCoach(Long coachId, Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<CoachBookingProjection> page = bookingRepository.findByCoachIdAndPaymentStatusAndEndTimeLessThan(coachId, PaymentStatus.PAID, OffsetDateTime.now(), pageable);
        if (Objects.nonNull(page)) {
            OffsetDateTime now = OffsetDateTime.now();

            List<CoachBookingResponse> response = page.getContent()
                    .stream()
                    .map(b -> {

                        BookingStatus status = resolveStatus(
                                b.getStartTime(),
                                b.getEndTime(),
                                now
                        );

                        return CoachBookingResponse.builder()
                                .id(b.getId())
                                .startTime(b.getStartTime())
                                .endTime(b.getEndTime())
                                .periodMinutes(b.getPeriodMinutes())
                                .price(b.getPrice())
                                .discount(b.getDiscount())
                                .finalPrice(b.getFinalPrice())
                                .coacheeFullName(b.getCoacheeFullName())
                                .coacheeProfileImageUrl(b.getCoacheeProfileImageUrl())
                                .status(status)
                                .statusWrapper(status != null ? StatusWrapper.builder().nameAr(status.getNameAr()).nameEn(status.getNameEn()).build() : null)
                                // actions
                                .actions(resolveActions(
                                        b.getStartTime(),
                                        b.getEndTime(),
                                        now
                                ))
                                .build();
                    })
                    .toList();

            return new ApiResponse<>(response, page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
        } else
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    public ApiResponse<List<CoacheeBookingResponse>> findUpcomingBookingForCoachee(Long coacheeId, Integer pageIndex, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        Page<CoacheeBookingProjection> page = bookingRepository.findByCoacheeIdAndPaymentStatusAndEndTimeGreaterThanEqual(coacheeId, PaymentStatus.PAID, OffsetDateTime.now(), pageable);
        if (Objects.nonNull(page)) {
            // ✅ 1. Collect coachIds
            List<Long> coachIds = page.getContent()
                    .stream()
                    .map(CoacheeBookingProjection::getCoachId)
                    .distinct()
                    .toList();

            // ✅ 2. Fetch industries in ONE query
            List<Object[]> result = coachIds.isEmpty()
                    ? Collections.emptyList()
                    : coachingIndustryRepository.findIndustriesByCoachIds(coachIds);

            // ✅ 3. Build industries map
            Map<Long, List<CoachingIndustryDto>> industriesMap = new HashMap<>();

            for (Object[] row : result) {
                Long coachId = (Long) row[0];
                CoachingIndustry ci = (CoachingIndustry) row[1];

                industriesMap
                        .computeIfAbsent(coachId, k -> new ArrayList<>())
                        .add(mapToCoachingIndustryDto(ci));
            }

            // ✅ 4. Build response
            List<CoacheeBookingResponse> response = page.getContent()
                    .stream()
                    .map(b -> {

                        BookingStatus status = BookingStatus.COMPLETED; // 🔥 always completed here

                        return CoacheeBookingResponse.builder()
                                .id(b.getId())
                                .startTime(b.getStartTime())
                                .endTime(b.getEndTime())
                                .periodMinutes(b.getPeriodMinutes())
                                .price(b.getPrice())
                                .discount(b.getDiscount())
                                .finalPrice(b.getFinalPrice())
                                .coachFullNameEn(b.getCoachFullNameEn())
                                .coachFullNameAr(b.getCoachFullNameAr())
                                .coachProfileImageUrl(b.getCoachProfileImageUrl())

                                .status(status)
                                .statusWrapper(StatusWrapper.builder()
                                        .nameEn(status.getNameEn())
                                        .nameAr(status.getNameAr())
                                        .build())

                                // ✅ completed → no actions (or add RATE later)
                                .actions(Collections.emptyList())

                                // ✅ industries from map
                                .coachIndustries(
                                        industriesMap.getOrDefault(
                                                b.getCoachId(),
                                                Collections.emptyList()
                                        )
                                )
                                .build();
                    })
                    .toList();

            return new ApiResponse<>(
                    response,
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.getSize(),
                    page.getNumber()
            );

        } else
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    public ApiResponse<List<CoacheeBookingResponse>> findPastBookingForCoachee(
            Long coacheeId,
            Integer pageIndex,
            Integer pageSize
    ) {

        Pageable pageable = PageRequest.of(pageIndex, pageSize);
        OffsetDateTime now = OffsetDateTime.now();

        Page<CoacheeBookingProjection> page =
                bookingRepository.findByCoacheeIdAndPaymentStatusAndEndTimeLessThan(
                        coacheeId,
                        PaymentStatus.PAID,
                        now,
                        pageable
                );
        if (Objects.nonNull(page)) {

            // ✅ 1. Collect coachIds
            List<Long> coachIds = page.getContent()
                    .stream()
                    .map(CoacheeBookingProjection::getCoachId)
                    .distinct()
                    .toList();

            // ✅ 2. Fetch industries in ONE query
            List<Object[]> result = coachIds.isEmpty()
                    ? Collections.emptyList()
                    : coachingIndustryRepository.findIndustriesByCoachIds(coachIds);

            // ✅ 3. Build industries map
            Map<Long, List<CoachingIndustryDto>> industriesMap = new HashMap<>();

            for (Object[] row : result) {
                Long coachId = (Long) row[0];
                CoachingIndustry ci = (CoachingIndustry) row[1];

                industriesMap
                        .computeIfAbsent(coachId, k -> new ArrayList<>())
                        .add(mapToCoachingIndustryDto(ci));
            }

            // ✅ 4. Build response
            List<CoacheeBookingResponse> response = page.getContent()
                    .stream()
                    .map(b -> {

                        BookingStatus status = BookingStatus.COMPLETED; // 🔥 always completed here

                        return CoacheeBookingResponse.builder()
                                .id(b.getId())
                                .startTime(b.getStartTime())
                                .endTime(b.getEndTime())
                                .periodMinutes(b.getPeriodMinutes())
                                .price(b.getPrice())
                                .discount(b.getDiscount())
                                .finalPrice(b.getFinalPrice())
                                .coachFullNameEn(b.getCoachFullNameEn())
                                .coachFullNameAr(b.getCoachFullNameAr())
                                .coachProfileImageUrl(b.getCoachProfileImageUrl())

                                .status(status)
                                .statusWrapper(StatusWrapper.builder()
                                        .nameEn(status.getNameEn())
                                        .nameAr(status.getNameAr())
                                        .build())

                                // ✅ completed → no actions (or add RATE later)
                                .actions(Collections.emptyList())

                                // ✅ industries from map
                                .coachIndustries(
                                        industriesMap.getOrDefault(
                                                b.getCoachId(),
                                                Collections.emptyList()
                                        )
                                )
                                .build();
                    })
                    .toList();

            return new ApiResponse<>(
                    response,
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.getSize(),
                    page.getNumber()
            );
        } else
            return new ApiResponse<>(new ArrayList<>(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    private List<ActionDto> resolveActions(
            OffsetDateTime start,
            OffsetDateTime end,
            OffsetDateTime now
    ) {

        List<ActionDto> actions = new ArrayList<>();

        boolean isCompleted = !now.isBefore(end); // safer edge-case handling

        // START → 15 mins before start until end
        if (!isCompleted) {
            OffsetDateTime startAllowedTime = start.minusMinutes(15);

            if (!now.isBefore(startAllowedTime) && now.isBefore(end)) {
                actions.add(mapAction(BookingAction.START));
            }
        }

        // CANCEL → only before 24h
        OffsetDateTime cancelDeadline = start.minusHours(24);

        if (now.isBefore(cancelDeadline)) {
            actions.add(mapAction(BookingAction.CANCEL));
        }

        return actions;
    }

    private ActionDto mapAction(BookingAction action) {
        return ActionDto.builder()
                .nameEn(action.getNameEn())
                .nameAr(action.getNameAr())
                .build();
    }

    private BookingStatus resolveStatus(
            OffsetDateTime start,
            OffsetDateTime end,
            OffsetDateTime now
    ) {
        if (now.isBefore(start)) {
            return BookingStatus.UPCOMING;
        } else if (!now.isBefore(end)) {
            return BookingStatus.COMPLETED;
        } else {
            return BookingStatus.RUNNING;
        }
    }

    private String resolvePortalBookingStatus(
            OffsetDateTime start,
            OffsetDateTime end,
            OffsetDateTime now
    ) {
        if (now.isBefore(start)) {
            return "UPCOMING";
        } else if (!now.isBefore(end)) {
            return "PAST";
        } else {
            return "RUNNING";
        }
    }

    private CoachingIndustryDto mapToCoachingIndustryDto(CoachingIndustry ci) {
        return CoachingIndustryDto.builder()
                .id(ci.getId())
                .nameEn(ci.getNameEn())
                .nameAr(ci.getNameAr())
                .build();
    }
}
