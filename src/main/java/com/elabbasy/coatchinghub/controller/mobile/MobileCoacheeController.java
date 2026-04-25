package com.elabbasy.coatchinghub.controller.mobile;

import com.elabbasy.coatchinghub.constant.Constants;
import com.elabbasy.coatchinghub.model.dto.CoacheeDto;
import com.elabbasy.coatchinghub.model.request.CreateCoacheeRequest;
import com.elabbasy.coatchinghub.model.request.UpdateCoacheeProfileRequest;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.CoachCoacheeResponse;
import com.elabbasy.coatchinghub.service.CoacheeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mobile/api/coachees")
@RequiredArgsConstructor
@Tag(name = "Mobile Coachee APIs", description = "Manage Coachees")
public class MobileCoacheeController {

    private final CoacheeService coacheeService;

    @Operation(summary = "Create a new coachee with user")
    @PostMapping("/register")
    public ApiResponse<CoacheeDto> createCoachee(@Valid @RequestBody CreateCoacheeRequest request) {
        CoacheeDto dto = coacheeService.createCoachee(request);
        return new ApiResponse<>(dto);
    }


    @Operation(summary = "get coachees list for coach")
    @GetMapping("/get-for-coach")
    public ApiResponse<List<CoachCoacheeResponse>> getForCoach(@RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId,
                                                               @RequestParam(required = false, defaultValue = "0") Integer pageIndex,
                                                               @RequestParam(required = false, defaultValue = Integer.MAX_VALUE+"") Integer pageSize,
                                                               @RequestParam(required = false) String name) {
        return coacheeService.findByCoach(coachId, pageIndex, pageSize, name);
    }

    @Operation(summary = "get coachee-details", description = "get-coachee-details")
    @GetMapping("/profile")
    public ApiResponse<CoacheeDto> getProfile(@RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId) {

        return new ApiResponse<>(coacheeService.details(coacheeId));
    }

    @PutMapping("/update-profile")
    public ApiResponse<CoacheeDto> updateProfile(@RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId,
                                                 @RequestBody @Valid UpdateCoacheeProfileRequest updateCoacheeProfileRequest) {
        return new ApiResponse<>(coacheeService.updateCoacheeProfile(coacheeId, updateCoacheeProfileRequest));
    }
}
