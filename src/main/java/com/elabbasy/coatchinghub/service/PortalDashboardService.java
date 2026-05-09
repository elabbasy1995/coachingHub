package com.elabbasy.coatchinghub.service;

import com.elabbasy.coatchinghub.model.entity.Booking;
import com.elabbasy.coatchinghub.model.enums.BookingStatus;
import com.elabbasy.coatchinghub.model.enums.CoachStatus;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import com.elabbasy.coatchinghub.model.enums.TaskAssignmentStatus;
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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortalDashboardService {

    private final CoachRepository coachRepository;
    private final CoacheeRepository coacheeRepository;
    private final BookingRepository bookingRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public PortalDashboardResponse getDashboard() {
        OffsetDateTime todayStart = LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime tomorrowStart = todayStart.plusDays(1);
        OffsetDateTime monthStart = LocalDate.now().withDayOfMonth(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        return new PortalDashboardResponse(
                buildCoachStats(),
                buildCoacheeStats(),
                buildBookingStats(todayStart, tomorrowStart, monthStart),
                buildRevenueStats(todayStart, tomorrowStart, monthStart)
        );
    }

    public PortalBookingStatusCountsResponse getBookingStatusCounts(LocalDate startDate, LocalDate endDate) {
        OffsetDateTime startDateTime = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endDateTime = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
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

    public PortalRevenueBetweenDatesResponse getRevenueBetweenDates(LocalDate startDate, LocalDate endDate) {
        OffsetDateTime startDateTime = startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime endDateTime = endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
        Double totalRevenue = defaultDouble(
                bookingRepository.sumFinalPriceByPaymentStatusAndStartTimeBetween(
                        PaymentStatus.PAID,
                        startDateTime,
                        endDateTime
                )
        );

        List<PortalRevenueBetweenDatesResponse.DailyRevenue> dailyRevenue = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            OffsetDateTime dayStart = currentDate.atStartOfDay().atOffset(ZoneOffset.UTC);
            OffsetDateTime dayEnd = currentDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
            dailyRevenue.add(new PortalRevenueBetweenDatesResponse.DailyRevenue(
                    currentDate,
                    defaultDouble(bookingRepository.sumFinalPriceByPaymentStatusAndStartTimeBetween(
                            PaymentStatus.PAID,
                            dayStart,
                            dayEnd
                    ))
            ));
            currentDate = currentDate.plusDays(1);
        }

        return new PortalRevenueBetweenDatesResponse(startDate, endDate, totalRevenue, dailyRevenue);
    }

    public Page<PortalDashboardTaskResponse> getTasksBetweenDates(LocalDate startDate,
                                                                  LocalDate endDate,
                                                                  Integer pageIndex,
                                                                  Integer pageSize) {
        return taskAssignmentRepository.findDashboardTasksBetweenDueDates(
                startDate,
                endDate,
                TaskAssignmentStatus.COMPLETED,
                PageRequest.of(pageIndex == null ? 0 : pageIndex, pageSize == null ? 20 : pageSize)
        );
    }

    public Page<PortalCoachBookingDashboardResponse> getCoachBookings(Integer pageIndex, Integer pageSize) {
        return coachRepository.findCoachBookingDashboard(
                PaymentStatus.PAID,
                PaymentStatus.PENDING,
                PaymentStatus.CANCELLED,
                OffsetDateTime.now(),
                PageRequest.of(pageIndex == null ? 0 : pageIndex, pageSize == null ? 20 : pageSize)
        );
    }

    public List<PortalIndustryPaidBookingCountResponse> getPaidBookingCountsByIndustry() {
        return bookingRepository.countPaidBookingsByCoachingIndustry(PaymentStatus.PAID);
    }

    private PortalDashboardResponse.CoachStats buildCoachStats() {
        return new PortalDashboardResponse.CoachStats(
                coachRepository.count(),
                coachRepository.countByStatus(CoachStatus.APPROVED),
                coachRepository.countByStatus(CoachStatus.PENDING_APPROVAL),
                coachRepository.countByStatus(CoachStatus.REJECTED)
        );
    }

    private PortalDashboardResponse.CoacheeStats buildCoacheeStats() {
        long total = coacheeRepository.count();
        long active = coacheeRepository.countByActiveTrue();

        return new PortalDashboardResponse.CoacheeStats(
                total,
                active,
                total - active
        );
    }

    private PortalDashboardResponse.BookingStats buildBookingStats(OffsetDateTime todayStart,
                                                                   OffsetDateTime tomorrowStart,
                                                                   OffsetDateTime monthStart) {
        return new PortalDashboardResponse.BookingStats(
                bookingRepository.count(),
                bookingRepository.countByPaymentStatus(PaymentStatus.PAID),
                bookingRepository.countByPaymentStatus(PaymentStatus.PENDING),
                bookingRepository.countByPaymentStatus(PaymentStatus.CANCELLED),
                bookingRepository.countByPaymentStatus(PaymentStatus.REFUNDED),
                bookingRepository.countByStartTimeGreaterThanEqualAndStartTimeLessThan(todayStart, tomorrowStart),
                bookingRepository.countByStartTimeGreaterThanEqual(monthStart)
        );
    }

    private PortalDashboardResponse.RevenueStats buildRevenueStats(OffsetDateTime todayStart,
                                                                   OffsetDateTime tomorrowStart,
                                                                   OffsetDateTime monthStart) {
        return new PortalDashboardResponse.RevenueStats(
                defaultDouble(bookingRepository.sumFinalPriceByPaymentStatus(PaymentStatus.PAID)),
                defaultDouble(bookingRepository.sumFinalPriceByPaymentStatusAndStartTimeBetween(PaymentStatus.PAID, todayStart, tomorrowStart)),
                defaultDouble(bookingRepository.sumFinalPriceByPaymentStatusAndStartTimeGreaterThanEqual(PaymentStatus.PAID, monthStart))
        );
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
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), startDateTime),
                criteriaBuilder.lessThan(root.get("startTime"), endDateTime)
        );
    }

    private Specification<Booking> statusSpec(BookingStatus status,
                                              OffsetDateTime startDateTime,
                                              OffsetDateTime endDateTime,
                                              OffsetDateTime now) {
        return (root, query, criteriaBuilder) -> {
            Predicate dateRange = criteriaBuilder.and(
                    criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), startDateTime),
                    criteriaBuilder.lessThan(root.get("startTime"), endDateTime)
            );

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

            return criteriaBuilder.and(dateRange, statusPredicate);
        };
    }
}
