package com.elabbasy.coatchinghub.controller.portal;

import com.elabbasy.coatchinghub.constant.PortalPermissionExpressions;
import com.elabbasy.coatchinghub.model.request.PortalCouponRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.PortalCouponResponse;
import com.elabbasy.coatchinghub.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portal/api/coupons")
@RequiredArgsConstructor
public class PortalCouponController {

    private final CouponService couponService;

    @PostMapping
    @PreAuthorize(PortalPermissionExpressions.COUPONS)
    public ApiResponse<PortalCouponResponse> createCoupon(@RequestBody @Valid PortalCouponRequest request) {
        return new ApiResponse<>(couponService.createCoupon(request));
    }

    @GetMapping("/admin-list")
    @PreAuthorize(PortalPermissionExpressions.COUPONS)
    public ApiResponse<List<PortalCouponResponse>> findAllForAdmin(@RequestParam(required = false) String search,
                                                                   @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                                                   @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                                                   @RequestParam(required = false) String sortBy,
                                                                   @RequestParam(required = false) String sortDir) {
        return couponService.findAllForAdmin(search, pageIndex, pageSize, sortBy, sortDir);
    }

    @PutMapping("/{couponId}")
    @PreAuthorize(PortalPermissionExpressions.COUPONS)
    public ApiResponse<PortalCouponResponse> updateCoupon(@PathVariable Long couponId,
                                                          @RequestBody @Valid PortalCouponRequest request) {
        return new ApiResponse<>(couponService.updateCoupon(couponId, request));
    }

    @DeleteMapping("/{couponId}")
    @PreAuthorize(PortalPermissionExpressions.COUPONS)
    public ApiResponse<Void> softDeleteCoupon(@PathVariable Long couponId) {
        return couponService.softDeleteCoupon(couponId);
    }
}
