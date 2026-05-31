package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.model.entity.Booking;
import com.elabbasy.coatchinghub.model.enums.BookingStatus;
import com.elabbasy.coatchinghub.model.enums.CoachStatus;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import com.elabbasy.coatchinghub.model.enums.TaskAssignmentStatus;
import com.elabbasy.coatchinghub.model.response.PortalBookingReportResponse;
import com.elabbasy.coatchinghub.model.response.PortalBookingStatusCountsResponse;
import com.elabbasy.coatchinghub.model.response.PortalCoachBookingDashboardResponse;
import com.elabbasy.coatchinghub.model.response.PortalDashboardResponse;
import com.elabbasy.coatchinghub.model.response.PortalDashboardTaskResponse;
import com.elabbasy.coatchinghub.model.response.PortalIndustryPaidBookingCountResponse;
import com.elabbasy.coatchinghub.model.response.PortalRevenueBetweenDatesResponse;
import com.elabbasy.coatchinghub.repository.BookingRepository;
import com.elabbasy.coatchinghub.repository.CoacheeRepository;
import com.elabbasy.coatchinghub.repository.CoachRepository;
import com.elabbasy.coatchinghub.repository.TaskAssignmentRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PortalDashboardService {

    private final CoachRepository coachRepository;
    private final CoacheeRepository coacheeRepository;
    private final BookingRepository bookingRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public PortalDashboardResponse getDashboard(LocalDate startDate, LocalDate endDate) {
        OffsetDateTime startDateTime = startDateTime(startDate);
        OffsetDateTime endDateTime = endDateTime(endDate);
        LocalDateTime createdStartDateTime = createdStartDateTime(startDate);
        LocalDateTime createdEndDateTime = createdEndDateTime(endDate);

        return new PortalDashboardResponse(
                buildCoachStats(createdStartDateTime, createdEndDateTime),
                buildCoacheeStats(createdStartDateTime, createdEndDateTime),
                buildBookingStats(startDateTime, endDateTime),
                buildRevenueStats(startDateTime, endDateTime)
        );
    }

    public PortalBookingStatusCountsResponse getBookingStatusCounts(LocalDate startDate, LocalDate endDate) {
        OffsetDateTime startDateTime = startDateTime(startDate);
        OffsetDateTime endDateTime = endDateTime(endDate);
        OffsetDateTime now = OffsetDateTime.now();

        Long totalCount = bookingRepository.count(dateRangeSpec(startDateTime, endDateTime));

        List<PortalBookingStatusCountsResponse.StatusCount> statusCounts = List.of(
                buildStatusCount(BookingStatus.NOT_CONFIRMED, startDateTime, endDateTime, now),
                buildStatusCount(BookingStatus.CANCELED, startDateTime, endDateTime, now),
                buildStatusCount(BookingStatus.UPCOMING, startDateTime, endDateTime, now),
                buildStatusCount(BookingStatus.COMPLETED, startDateTime, endDateTime, now)
        );

        return new PortalBookingStatusCountsResponse(startDate, endDate, totalCount, statusCounts);
    }

    public PortalBookingReportResponse getBookingReport(LocalDate startDate, LocalDate endDate) {
        OffsetDateTime startDateTime = startDateTime(startDate);
        OffsetDateTime endDateTime = endDateTime(endDate);
        OffsetDateTime now = OffsetDateTime.now();

        Long totalCount = bookingRepository.count(dateRangeSpec(startDateTime, endDateTime));
        Long completedCount = bookingRepository.count(completedBookingSpec(startDateTime, endDateTime, now));

        return new PortalBookingReportResponse(
                startDate,
                endDate,
                totalCount,
                completedCount,
                totalCount - completedCount
        );
    }

    public PortalRevenueBetweenDatesResponse getRevenueBetweenDates(LocalDate startDate, LocalDate endDate) {
        LocalDate effectiveStartDate = startDate;
        LocalDate effectiveEndDate = endDate;

        if (startDate == null && endDate == null) {
            LocalDate currentMonthEndDate = YearMonth.from(LocalDate.now(ZoneOffset.UTC)).atEndOfMonth();
            Booking firstPaidBooking = bookingRepository.findFirstByPaymentStatusOrderByStartTimeAsc(PaymentStatus.PAID);
            effectiveStartDate = firstPaidBooking == null
                    ? currentMonthEndDate.withDayOfMonth(1)
                    : firstPaidBooking.getStartTime().toLocalDate().withDayOfMonth(1);
            effectiveEndDate = currentMonthEndDate;
        }

        OffsetDateTime startDateTime = startDateTime(effectiveStartDate);
        OffsetDateTime endDateTime = endDateTime(effectiveEndDate);
        Double totalRevenue = sumPaidRevenue(startDateTime, endDateTime);

        List<PortalRevenueBetweenDatesResponse.MonthlyRevenue> monthlyRevenue = buildMonthlyRevenue(effectiveStartDate, effectiveEndDate);

        return new PortalRevenueBetweenDatesResponse(effectiveStartDate, effectiveEndDate, totalRevenue, monthlyRevenue);
    }

    public Page<PortalDashboardTaskResponse> getTasksBetweenDates(LocalDate startDate,
                                                                  LocalDate endDate,
                                                                  Integer pageIndex,
                                                                  Integer pageSize) {
        return taskAssignmentRepository.findDashboardTasksBetweenDueDates(
                startDate != null,
                startDate,
                endDate != null,
                endDate,
                TaskAssignmentStatus.COMPLETED,
                PageRequest.of(pageIndex == null ? 0 : pageIndex, pageSize == null ? 20 : pageSize)
        );
    }

    public Page<PortalCoachBookingDashboardResponse> getCoachBookings(LocalDate startDate,
                                                                      LocalDate endDate,
                                                                      Integer pageIndex,
                                                                      Integer pageSize) {
        OffsetDateTime startDateTime = startDateTime(startDate);
        OffsetDateTime endDateTime = endDateTime(endDate);

        return coachRepository.findCoachBookingDashboard(
                PaymentStatus.PAID,
                PaymentStatus.PENDING,
                PaymentStatus.CANCELLED,
                OffsetDateTime.now(),
                startDateTime != null,
                endDateTime != null,
                startDateTime,
                endDateTime,
                PageRequest.of(pageIndex == null ? 0 : pageIndex, pageSize == null ? 20 : pageSize)
        );
    }

    public List<PortalIndustryPaidBookingCountResponse> getPaidBookingCountsByIndustry(LocalDate startDate, LocalDate endDate) {
        OffsetDateTime startDateTime = startDateTime(startDate);
        OffsetDateTime endDateTime = endDateTime(endDate);

        if (startDateTime != null && endDateTime != null) {
            return bookingRepository.countPaidBookingsByCoachingIndustryBetween(PaymentStatus.PAID, startDateTime, endDateTime);
        }
        if (startDateTime != null) {
            return bookingRepository.countPaidBookingsByCoachingIndustryFrom(PaymentStatus.PAID, startDateTime);
        }
        if (endDateTime != null) {
            return bookingRepository.countPaidBookingsByCoachingIndustryBefore(PaymentStatus.PAID, endDateTime);
        }
        return bookingRepository.countPaidBookingsByCoachingIndustry(PaymentStatus.PAID);
    }

    private PortalDashboardResponse.CoachStats buildCoachStats(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return new PortalDashboardResponse.CoachStats(
                coachRepository.countByCreatedDateOptionalRange(startDateTime, endDateTime),
                coachRepository.countByStatusAndCreatedDateOptionalRange(CoachStatus.APPROVED, startDateTime, endDateTime),
                coachRepository.countByStatusAndCreatedDateOptionalRange(CoachStatus.PENDING_APPROVAL, startDateTime, endDateTime),
                coachRepository.countByStatusAndCreatedDateOptionalRange(CoachStatus.REJECTED, startDateTime, endDateTime)
        );
    }

    private PortalDashboardResponse.CoacheeStats buildCoacheeStats(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        long total = coacheeRepository.countByCreatedDateOptionalRange(startDateTime, endDateTime);
        long active = coacheeRepository.countByActiveTrueAndCreatedDateOptionalRange(startDateTime, endDateTime);

        return new PortalDashboardResponse.CoacheeStats(
                total,
                active,
                total - active
        );
    }

    private PortalDashboardResponse.BookingStats buildBookingStats(OffsetDateTime startDateTime,
                                                                   OffsetDateTime endDateTime) {
        return new PortalDashboardResponse.BookingStats(
                bookingRepository.count(dateRangeSpec(startDateTime, endDateTime)),
                bookingRepository.count(paymentStatusDateRangeSpec(PaymentStatus.PAID, startDateTime, endDateTime)),
                bookingRepository.count(paymentStatusDateRangeSpec(PaymentStatus.PENDING, startDateTime, endDateTime)),
                bookingRepository.count(paymentStatusDateRangeSpec(PaymentStatus.CANCELLED, startDateTime, endDateTime)),
                bookingRepository.count(paymentStatusDateRangeSpec(PaymentStatus.REFUNDED, startDateTime, endDateTime)),
                bookingRepository.count(dateRangeSpec(startDateTime, endDateTime)),
                bookingRepository.count(dateRangeSpec(startDateTime, endDateTime))
        );
    }

    private PortalDashboardResponse.RevenueStats buildRevenueStats(OffsetDateTime startDateTime,
                                                                   OffsetDateTime endDateTime) {
        Double paidRevenue = sumPaidRevenue(startDateTime, endDateTime);
        return new PortalDashboardResponse.RevenueStats(
                paidRevenue,
                paidRevenue,
                paidRevenue
        );
    }

    private Double sumPaidRevenue(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
        if (startDateTime != null && endDateTime != null) {
            return defaultDouble(bookingRepository.sumFinalPriceByPaymentStatusAndStartTimeBetween(
                    PaymentStatus.PAID,
                    startDateTime,
                    endDateTime
            ));
        }
        if (startDateTime != null) {
            return defaultDouble(bookingRepository.sumFinalPriceByPaymentStatusAndStartTimeGreaterThanEqual(
                    PaymentStatus.PAID,
                    startDateTime
            ));
        }
        if (endDateTime != null) {
            return defaultDouble(bookingRepository.sumFinalPriceByPaymentStatusAndStartTimeLessThan(
                    PaymentStatus.PAID,
                    endDateTime
            ));
        }
        return defaultDouble(bookingRepository.sumFinalPriceByPaymentStatus(PaymentStatus.PAID));
    }

    private Double defaultDouble(Double value) {
        return value == null ? 0.0 : value;
    }

    private PortalBookingStatusCountsResponse.StatusCount buildStatusCount(BookingStatus status,
                                                                           OffsetDateTime startDateTime,
                                                                           OffsetDateTime endDateTime,
                                                                           OffsetDateTime now) {
        Long count = bookingRepository.count(statusSpec(status, startDateTime, endDateTime, now));
        return new PortalBookingStatusCountsResponse.StatusCount(
                status,
                status.getNameEn(),
                status.getNameAr(),
                count
        );
    }

    private Specification<Booking> dateRangeSpec(OffsetDateTime startDateTime, OffsetDateTime endDateTime) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            addStartTimePredicates(predicates, root.get("startTime"), criteriaBuilder, startDateTime, endDateTime);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private OffsetDateTime startDateTime(LocalDate startDate) {
        return startDate == null ? null : startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    private OffsetDateTime endDateTime(LocalDate endDate) {
        return endDate == null ? null : endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    private Specification<Booking> statusSpec(BookingStatus status,
                                              OffsetDateTime startDateTime,
                                              OffsetDateTime endDateTime,
                                              OffsetDateTime now) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            addStartTimePredicates(predicates, root.get("startTime"), criteriaBuilder, startDateTime, endDateTime);

            Predicate confirmed = criteriaBuilder.and(
                    criteriaBuilder.notEqual(root.get("paymentStatus"), PaymentStatus.CANCELLED),
                    criteriaBuilder.notEqual(root.get("paymentStatus"), PaymentStatus.PENDING)
            );

            Predicate statusPredicate = switch (status) {
                case NOT_CONFIRMED -> criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.PENDING);
                case CANCELED -> criteriaBuilder.equal(root.get("paymentStatus"), PaymentStatus.CANCELLED);
                case UPCOMING -> criteriaBuilder.and(
                        confirmed,
                        criteriaBuilder.greaterThan(root.get("startTime"), now)
                );
                case COMPLETED -> criteriaBuilder.and(
                        confirmed,
                        criteriaBuilder.lessThanOrEqualTo(root.get("startTime"), now)
                );
                case RUNNING, PAST -> criteriaBuilder.disjunction();
            };

            predicates.add(statusPredicate);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<Booking> completedBookingSpec(OffsetDateTime startDateTime,
                                                        OffsetDateTime endDateTime,
                                                        OffsetDateTime now) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            addStartTimePredicates(predicates, root.get("startTime"), criteriaBuilder, startDateTime, endDateTime);
            predicates.add(criteriaBuilder.notEqual(root.get("paymentStatus"), PaymentStatus.CANCELLED));
            predicates.add(criteriaBuilder.notEqual(root.get("paymentStatus"), PaymentStatus.PENDING));
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startTime"), now));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private LocalDateTime createdStartDateTime(LocalDate startDate) {
        return startDate == null ? null : startDate.atStartOfDay();
    }

    private LocalDateTime createdEndDateTime(LocalDate endDate) {
        return endDate == null ? null : endDate.plusDays(1).atStartOfDay();
    }

    private Specification<Booking> paymentStatusDateRangeSpec(PaymentStatus paymentStatus,
                                                              OffsetDateTime startDateTime,
                                                              OffsetDateTime endDateTime) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("paymentStatus"), paymentStatus));
            addStartTimePredicates(predicates, root.get("startTime"), criteriaBuilder, startDateTime, endDateTime);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private List<PortalRevenueBetweenDatesResponse.MonthlyRevenue> buildMonthlyRevenue(LocalDate startDate, LocalDate endDate) {
        List<PortalRevenueBetweenDatesResponse.MonthlyRevenue> monthlyRevenue = new ArrayList<>();
        if (startDate == null || endDate == null) {
            return monthlyRevenue;
        }

        LocalDate currentMonth = startDate.withDayOfMonth(1);
        while (!currentMonth.isAfter(endDate)) {
            LocalDate monthStart = currentMonth.isBefore(startDate) ? startDate : currentMonth;
            LocalDate nextMonth = currentMonth.plusMonths(1);
            LocalDate monthEndExclusive = nextMonth.isAfter(endDate.plusDays(1)) ? endDate.plusDays(1) : nextMonth;
            OffsetDateTime monthStartDateTime = monthStart.atStartOfDay().atOffset(ZoneOffset.UTC);
            OffsetDateTime monthEndDateTime = monthEndExclusive.atStartOfDay().atOffset(ZoneOffset.UTC);
            monthlyRevenue.add(new PortalRevenueBetweenDatesResponse.MonthlyRevenue(
                    currentMonth,
                    currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                    currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.forLanguageTag("ar")),
                    sumPaidRevenue(monthStartDateTime, monthEndDateTime)
            ));
            currentMonth = nextMonth;
        }
        return monthlyRevenue;
    }

    private void addStartTimePredicates(List<Predicate> predicates,
                                        jakarta.persistence.criteria.Path<OffsetDateTime> startTime,
                                        jakarta.persistence.criteria.CriteriaBuilder criteriaBuilder,
                                        OffsetDateTime startDateTime,
                                        OffsetDateTime endDateTime) {
        if (startDateTime != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(startTime, startDateTime));
        }
        if (endDateTime != null) {
            predicates.add(criteriaBuilder.lessThan(startTime, endDateTime));
        }
    }
}
