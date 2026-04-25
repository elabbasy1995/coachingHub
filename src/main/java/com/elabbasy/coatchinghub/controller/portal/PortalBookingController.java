package com.elabbasy.coatchinghub.controller.portal;

import com.elabbasy.coatchinghub.constant.PortalPermissionExpressions;
import com.elabbasy.coatchinghub.model.dto.BookingDto;
import com.elabbasy.coatchinghub.model.request.RescheduleBookingRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.PortalBookingListResponse;
import com.elabbasy.coatchinghub.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/portal/api/bookings")
@RequiredArgsConstructor
public class PortalBookingController {

    private final BookingService bookingService;

    @GetMapping("/admin-list")
    @PreAuthorize(PortalPermissionExpressions.BOOKING)
    public ApiResponse<List<PortalBookingListResponse>> findAllForAdmin(@RequestParam(required = false) String search,
                                                                        @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                                                        @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                                                        @RequestParam(required = false) String sortBy,
                                                                        @RequestParam(required = false) String sortDir) {
        return bookingService.findAllForAdmin(search, pageIndex, pageSize, sortBy, sortDir);
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
        return new ApiResponse<>(bookingService.refundBookingByAdmin(bookingId));
    }
}
