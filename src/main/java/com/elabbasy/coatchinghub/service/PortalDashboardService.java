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

    public PortalDashboardResponse getDashboard(LocalDate startDate, LocalDate endDate) {
        OffsetDateTime startDateTime = startDateTime(startDate);
        OffsetDateTime endDateTime = endDateTime(endDate);
        LocalDateTime createdStartDateTime = startDate.atStartOfDay();
        LocalDateTime createdEndDateTime = endDate.plusDays(1).atStartOfDay();

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

        Long totalCount = bookingRepository.countByStartTimeGreaterThanEqualAndStartTimeLessThan(startDateTime, endDateTime);
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
        OffsetDateTime startDateTime = startDateTime(startDate);
        OffsetDateTime endDateTime = endDateTime(endDate);
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

    public Page<PortalCoachBookingDashboardResponse> getCoachBookings(LocalDate startDate,
                                                                      LocalDate endDate,
                                                                      Integer pageIndex,
                                                                      Integer pageSize) {
        return coachRepository.findCoachBookingDashboard(
                PaymentStatus.PAID,
                PaymentStatus.PENDING,
                PaymentStatus.CANCELLED,
                OffsetDateTime.now(),
                startDateTime(startDate),
                endDateTime(endDate),
                PageRequest.of(pageIndex == null ? 0 : pageIndex, pageSize == null ? 20 : pageSize)
        );
    }

    public List<PortalIndustryPaidBookingCountResponse> getPaidBookingCountsByIndustry(LocalDate startDate, LocalDate endDate) {
        return bookingRepository.countPaidBookingsByCoachingIndustry(
                PaymentStatus.PAID,
                startDateTime(startDate),
                endDateTime(endDate)
        );
    }

    private PortalDashboardResponse.CoachStats buildCoachStats(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return new PortalDashboardResponse.CoachStats(
                coachRepository.countByCreatedDateGreaterThanEqualAndCreatedDateLessThan(startDateTime, endDateTime),
                coachRepository.countByStatusAndCreatedDateGreaterThanEqualAndCreatedDateLessThan(CoachStatus.APPROVED, startDateTime, endDateTime),
                coachRepository.countByStatusAndCreatedDateGreaterThanEqualAndCreatedDateLessThan(CoachStatus.PENDING_APPROVAL, startDateTime, endDateTime),
                coachRepository.countByStatusAndCreatedDateGreaterThanEqualAndCreatedDateLessThan(CoachStatus.REJECTED, startDateTime, endDateTime)
        );
    }

    private PortalDashboardResponse.CoacheeStats buildCoacheeStats(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        long total = coacheeRepository.countByCreatedDateGreaterThanEqualAndCreatedDateLessThan(startDateTime, endDateTime);
        long active = coacheeRepository.countByActiveTrueAndCreatedDateGreaterThanEqualAndCreatedDateLessThan(startDateTime, endDateTime);

        return new PortalDashboardResponse.CoacheeStats(
                total,
                active,
                total - active
        );
    }

    private PortalDashboardResponse.BookingStats buildBookingStats(OffsetDateTime startDateTime,
                                                                   OffsetDateTime endDateTime) {
        return new PortalDashboardResponse.BookingStats(
                bookingRepository.countByStartTimeGreaterThanEqualAndStartTimeLessThan(startDateTime, endDateTime),
                bookingRepository.countByPaymentStatusAndStartTimeGreaterThanEqualAndStartTimeLessThan(PaymentStatus.PAID, startDateTime, endDateTime),
                bookingRepository.countByPaymentStatusAndStartTimeGreaterThanEqualAndStartTimeLessThan(PaymentStatus.PENDING, startDateTime, endDateTime),
                bookingRepository.countByPaymentStatusAndStartTimeGreaterThanEqualAndStartTimeLessThan(PaymentStatus.CANCELLED, startDateTime, endDateTime),
                bookingRepository.countByPaymentStatusAndStartTimeGreaterThanEqualAndStartTimeLessThan(PaymentStatus.REFUNDED, startDateTime, endDateTime),
                bookingRepository.countByStartTimeGreaterThanEqualAndStartTimeLessThan(startDateTime, endDateTime),
                bookingRepository.countByStartTimeGreaterThanEqualAndStartTimeLessThan(startDateTime, endDateTime)
        );
    }

    private PortalDashboardResponse.RevenueStats buildRevenueStats(OffsetDateTime startDateTime,
                                                                   OffsetDateTime endDateTime) {
        return new PortalDashboardResponse.RevenueStats(
                defaultDouble(bookingRepository.sumFinalPriceByPaymentStatusAndStartTimeBetween(PaymentStatus.PAID, startDateTime, endDateTime)),
                defaultDouble(bookingRepository.sumFinalPriceByPaymentStatusAndStartTimeBetween(PaymentStatus.PAID, startDateTime, endDateTime)),
                defaultDouble(bookingRepository.sumFinalPriceByPaymentStatusAndStartTimeBetween(PaymentStatus.PAID, startDateTime, endDateTime))
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

    private OffsetDateTime startDateTime(LocalDate startDate) {
        return startDate.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    private OffsetDateTime endDateTime(LocalDate endDate) {
        return endDate.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
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

    private Specification<Booking> completedBookingSpec(OffsetDateTime startDateTime,
                                                        OffsetDateTime endDateTime,
                                                        OffsetDateTime now) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.greaterThanOrEqualTo(root.get("startTime"), startDateTime),
                criteriaBuilder.lessThan(root.get("startTime"), endDateTime),
                criteriaBuilder.notEqual(root.get("paymentStatus"), PaymentStatus.CANCELLED),
                criteriaBuilder.notEqual(root.get("paymentStatus"), PaymentStatus.PENDING),
                criteriaBuilder.lessThanOrEqualTo(root.get("startTime"), now)
        );
    }
}
