package com.elabbasy.coatchinghub.controller.portal;

import com.elabbasy.coatchinghub.constant.PortalPermissionExpressions;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.PortalBookingStatusCountsResponse;
import com.elabbasy.coatchinghub.model.response.PortalCoachBookingDashboardResponse;
import com.elabbasy.coatchinghub.model.response.PortalDashboardResponse;
import com.elabbasy.coatchinghub.model.response.PortalDashboardTaskResponse;
import com.elabbasy.coatchinghub.model.response.PortalIndustryPaidBookingCountResponse;
import com.elabbasy.coatchinghub.model.response.PortalRevenueBetweenDatesResponse;
import com.elabbasy.coatchinghub.service.PortalDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/portal/api/dashboard")
@RequiredArgsConstructor
public class PortalDashboardController {

    private final PortalDashboardService portalDashboardService;

    @GetMapping
    @PreAuthorize(PortalPermissionExpressions.DASHBOARD)
    public ApiResponse<PortalDashboardResponse> getDashboard() {
        return new ApiResponse<>(portalDashboardService.getDashboard());
    }

    @GetMapping("/booking-status-counts")
    @PreAuthorize(PortalPermissionExpressions.DASHBOARD)
    public ApiResponse<PortalBookingStatusCountsResponse> getBookingStatusCounts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return new ApiResponse<>(portalDashboardService.getBookingStatusCounts(startDate, endDate));
    }

    @GetMapping("/revenue")
    @PreAuthorize(PortalPermissionExpressions.DASHBOARD)
    public ApiResponse<PortalRevenueBetweenDatesResponse> getRevenueBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return new ApiResponse<>(portalDashboardService.getRevenueBetweenDates(startDate, endDate));
    }

    @GetMapping("/tasks")
    @PreAuthorize(PortalPermissionExpressions.DASHBOARD)
    public ApiResponse<List<PortalDashboardTaskResponse>> getTasksBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        Page<PortalDashboardTaskResponse> page = portalDashboardService.getTasksBetweenDates(startDate, endDate, pageIndex, pageSize);
        return new ApiResponse<>(page.getContent(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    @GetMapping("/coach-bookings")
    @PreAuthorize(PortalPermissionExpressions.DASHBOARD)
    public ApiResponse<List<PortalCoachBookingDashboardResponse>> getCoachBookings(
            @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        Page<PortalCoachBookingDashboardResponse> page = portalDashboardService.getCoachBookings(pageIndex, pageSize);
        return new ApiResponse<>(page.getContent(), page.getTotalElements(), page.getTotalPages(), page.getSize(), page.getNumber());
    }

    @GetMapping("/industry-paid-bookings")
    @PreAuthorize(PortalPermissionExpressions.DASHBOARD)
    public ApiResponse<List<PortalIndustryPaidBookingCountResponse>> getPaidBookingCountsByIndustry() {
        return new ApiResponse<>(portalDashboardService.getPaidBookingCountsByIndustry());
    }
}
