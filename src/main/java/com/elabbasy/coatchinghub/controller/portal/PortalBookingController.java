package com.elabbasy.coatchinghub.controller.portal;

import com.elabbasy.coatchinghub.constant.PortalPermissionExpressions;
import com.elabbasy.coatchinghub.model.dto.BookingDto;
import com.elabbasy.coatchinghub.model.enums.BookingStatus;
import com.elabbasy.coatchinghub.model.enums.PaymentStatus;
import com.elabbasy.coatchinghub.model.request.RescheduleBookingRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.PortalBookingListResponse;
import com.elabbasy.coatchinghub.service.BookingService;
import com.elabbasy.coatchinghub.service.StripePaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/portal/api/bookings")
@RequiredArgsConstructor
public class PortalBookingController {

    private final BookingService bookingService;
    private final StripePaymentService stripePaymentService;

    @GetMapping("/admin-list")
    @PreAuthorize(PortalPermissionExpressions.BOOKING)
    public ApiResponse<List<PortalBookingListResponse>> findAllForAdmin(@RequestParam(required = false) String search,
                                                                        @RequestParam(required = false) Long coachId,
                                                                        @RequestParam(required = false) Long coacheeId,
                                                                        @RequestParam(required = false) String transaction,
                                                                        @RequestParam(required = false) PaymentStatus paymentStatus,
                                                                        @RequestParam(required = false) BookingStatus bookingStatus,
                                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                        @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                                                        @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                                                        @RequestParam(required = false) String sortBy,
                                                                        @RequestParam(required = false) String sortDir) {
        return bookingService.findAllForAdmin(search, coachId, coacheeId, transaction, paymentStatus, bookingStatus,
                startDate, endDate, pageIndex, pageSize, sortBy, sortDir);
    }

    @GetMapping("/{bookingId}")
    @PreAuthorize(PortalPermissionExpressions.BOOKING)
    public ApiResponse<BookingDto> getDetailsForAdmin(@PathVariable Long bookingId) {
        return new ApiResponse<>(bookingService.getDetailsForAdmin(bookingId));
    }

    @PostMapping("/reschedule")
    @PreAuthorize(PortalPermissionExpressions.BOOKING)
    public ApiResponse<BookingDto> rescheduleBooking(@RequestBody @Valid RescheduleBookingRequest request) {
        return new ApiResponse<>(bookingService.rescheduleBookingByAdmin(request));
    }

    @PutMapping("/{bookingId}/cancel")
    @PreAuthorize(PortalPermissionExpressions.BOOKING)
    public ApiResponse<BookingDto> cancelBooking(@PathVariable Long bookingId) {
        return new ApiResponse<>(bookingService.cancelBookingByAdmin(bookingId));
    }

    @PutMapping("/{bookingId}/refund")
    @PreAuthorize(PortalPermissionExpressions.BOOKING)
    public ApiResponse<BookingDto> refundBooking(@PathVariable Long bookingId) {
        return new ApiResponse<>(stripePaymentService.refundBookingByAdmin(bookingId));
    }
}
