package com.elabbasy.coatchinghub.controller.portal;

import com.elabbasy.coatchinghub.constant.PortalPermissionExpressions;
import com.elabbasy.coatchinghub.model.dto.CoachDto;
import com.elabbasy.coatchinghub.model.request.ApproveCoachRequest;
import com.elabbasy.coatchinghub.model.request.UpdatePortalCoachRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.PortalCoachDetailsResponse;
import com.elabbasy.coatchinghub.model.response.PortalCoachLookupProjection;
import com.elabbasy.coatchinghub.model.response.PortalCoachListResponse;
import com.elabbasy.coatchinghub.service.CoachService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/portal/api/coaches")
@RequiredArgsConstructor
public class PortalCoachController {

    private final CoachService coachService;

    @PutMapping("/approve/{coachId}")
    @PreAuthorize(PortalPermissionExpressions.COACHES)
    public ApiResponse<CoachDto> approve(@PathVariable Long coachId, @RequestBody ApproveCoachRequest approveCoachRequest) {
        return new ApiResponse<>(coachService.approveCoach(coachId, approveCoachRequest));
    }

    @PutMapping("/reject/{coachId}")
    @PreAuthorize(PortalPermissionExpressions.COACHES)
    public ApiResponse<CoachDto> reject(@PathVariable Long coachId) {
        return new ApiResponse<>(coachService.rejectCoach(coachId));
    }

    @GetMapping("/admin-list")
    @PreAuthorize(PortalPermissionExpressions.COACHES)
    public ApiResponse<List<PortalCoachListResponse>> findAllForAdmin(@RequestParam(required = false) String name,
                                                                       @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                                                       @RequestParam(required = false, defaultValue = "20") Integer pageSize,
                                                                       @RequestParam(required = false) String sortBy,
                                                                       @RequestParam(required = false) String sortDir) {
        return coachService.findAllForAdmin(name, pageIndex, pageSize, sortBy, sortDir);
    }

    @GetMapping("/coaches-lookup")
    @PreAuthorize(PortalPermissionExpressions.COACHES_OR_BOOKING)
    public ApiResponse<List<PortalCoachLookupProjection>> findApprovedCoachLookup() {
        return coachService.findApprovedCoachLookup();
    }

    @GetMapping("/{coachId}")
    @PreAuthorize(PortalPermissionExpressions.COACHES)
    public ApiResponse<PortalCoachDetailsResponse> getDetailsForAdmin(@PathVariable Long coachId) {
        return new ApiResponse<>(coachService.getDetailsForAdmin(coachId));
    }

    @PutMapping("/{coachId}")
    @PreAuthorize(PortalPermissionExpressions.COACHES)
    public ApiResponse<PortalCoachDetailsResponse> updateCoachForAdmin(@PathVariable Long coachId,
                                                                       @RequestBody @jakarta.validation.Valid UpdatePortalCoachRequest request) {
        return new ApiResponse<>(coachService.updateCoachForAdmin(coachId, request));
    }
    @PutMapping("/{coachId}/enable")
    @PreAuthorize(PortalPermissionExpressions.COACHES)
    public ApiResponse<PortalCoachDetailsResponse> enableCoach(@PathVariable Long coachId) {
        return new ApiResponse<>(coachService.enableCoach(coachId));
    }

    @PutMapping("/{coachId}/disable")
    @PreAuthorize(PortalPermissionExpressions.COACHES)
    public ApiResponse<PortalCoachDetailsResponse> disableCoach(@PathVariable Long coachId) {
        return new ApiResponse<>(coachService.disableCoach(coachId));
    }

    @PostMapping("/{coachId}/reset-password")
    @PreAuthorize(PortalPermissionExpressions.COACHES)
    public ApiResponse<String> resetCoachPassword(@PathVariable Long coachId) {
        return new ApiResponse<>(coachService.resetCoachPassword(coachId));
    }
}
