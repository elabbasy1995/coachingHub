package com.elabbasy.coatchinghub.controller.portal;

import com.elabbasy.coatchinghub.constant.Constants;
import com.elabbasy.coatchinghub.constant.PortalPermissionExpressions;
import com.elabbasy.coatchinghub.model.dto.AdminDto;
import com.elabbasy.coatchinghub.model.request.InvitePortalAdminRequest;
import com.elabbasy.coatchinghub.model.request.UpdatePortalAdminRequest;
import com.elabbasy.coatchinghub.model.request.UpdatePortalAdminProfileRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.PortalAdminDetailsResponse;
import com.elabbasy.coatchinghub.model.response.PortalAdminListResponse;
import com.elabbasy.coatchinghub.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/portal/api/admins")
@RequiredArgsConstructor
public class PortalAdminController {

    private final AdminService adminService;

    @PostMapping("/invite")
    @PreAuthorize(PortalPermissionExpressions.ADMINS)
    public ApiResponse<AdminDto> inviteAdmin(@RequestBody @Valid InvitePortalAdminRequest request) {
        return new ApiResponse<>(adminService.inviteAdmin(request));
    }

    @GetMapping("/admin-list")
    @PreAuthorize(PortalPermissionExpressions.ADMINS)
    public ApiResponse<List<PortalAdminListResponse>> findAllForAdmin(@RequestParam(required = false) String search,
                                                                      @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                                                      @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                                                      @RequestParam(required = false) String sortBy,
                                                                      @RequestParam(required = false) String sortDir) {
        return adminService.findAllForAdmin(search, pageIndex, pageSize, sortBy, sortDir);
    }

    @GetMapping("/{adminId}")
    @PreAuthorize(PortalPermissionExpressions.ADMINS)
    public ApiResponse<PortalAdminDetailsResponse> getAdminDetails(@PathVariable Long adminId) {
        return new ApiResponse<>(adminService.getAdminDetails(adminId));
    }

    @GetMapping("/profile")
    public ApiResponse<PortalAdminDetailsResponse> getMyProfile(@RequestAttribute(name = Constants.ADMIN_ID_ATTRIBUTE) Long adminId) {
        return new ApiResponse<>(adminService.getMyProfile(adminId));
    }

    @PutMapping("/{adminId}")
    @PreAuthorize(PortalPermissionExpressions.ADMINS)
    public ApiResponse<PortalAdminListResponse> updateAdmin(@PathVariable Long adminId,
                                                            @RequestBody @Valid UpdatePortalAdminRequest request) {
        return new ApiResponse<>(adminService.updateAdmin(adminId, request));
    }

    @PutMapping("/{adminId}/enable")
    @PreAuthorize(PortalPermissionExpressions.ADMINS)
    public ApiResponse<PortalAdminDetailsResponse> enableAdmin(@PathVariable Long adminId) {
        return new ApiResponse<>(adminService.enableAdmin(adminId));
    }

    @PutMapping("/{adminId}/disable")
    @PreAuthorize(PortalPermissionExpressions.ADMINS)
    public ApiResponse<PortalAdminDetailsResponse> disableAdmin(@PathVariable Long adminId) {
        return new ApiResponse<>(adminService.disableAdmin(adminId));
    }

    @PostMapping("/{adminId}/reset-password")
    @PreAuthorize(PortalPermissionExpressions.ADMINS)
    public ApiResponse<String> resetAdminPassword(@PathVariable Long adminId) {
        return new ApiResponse<>(adminService.resetAdminPassword(adminId));
    }

    @PutMapping("/profile")
    public ApiResponse<PortalAdminDetailsResponse> updateProfile(@RequestAttribute(name = Constants.ADMIN_ID_ATTRIBUTE) Long adminId,
                                                                 @RequestBody @Valid UpdatePortalAdminProfileRequest request) {
        return new ApiResponse<>(adminService.updateMyProfile(adminId, request));
    }

    @DeleteMapping("/{adminId}")
    @PreAuthorize(PortalPermissionExpressions.ADMINS)
    public ApiResponse<Void> deleteAdmin(@PathVariable Long adminId) {
        return adminService.deleteAdmin(adminId);
    }
}
