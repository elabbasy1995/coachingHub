package com.elabbasy.coatchinghub.controller.portal;

import com.elabbasy.coatchinghub.constant.PortalPermissionExpressions;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.DaySlotsDto;
import com.elabbasy.coatchinghub.service.CoachSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/portal/api/coach-slots")
@RequiredArgsConstructor
public class PortalCoachSlotController {

    private final CoachSlotService coachSlotService;

    @GetMapping("/{coachId}")
    @PreAuthorize(PortalPermissionExpressions.COACHES_OR_BOOKING)
    public ApiResponse<List<DaySlotsDto>> getCoachSlots(@PathVariable Long coachId) {
        return new ApiResponse<>(coachSlotService.getSlotsByDay(coachId));
    }

    @GetMapping("/available/{coachId}")
    @PreAuthorize(PortalPermissionExpressions.COACHES_OR_BOOKING)
    public ApiResponse<List<DaySlotsDto>> getAvailableCoachSlots(@PathVariable Long coachId) {
        return new ApiResponse<>(coachSlotService.getAvailableSlotsByDay(coachId));
    }
}
