package com.elabbasy.coatchinghub.controller.mobile;

import com.elabbasy.coatchinghub.constant.Constants;
import com.elabbasy.coatchinghub.model.dto.CoachDto;
import com.elabbasy.coatchinghub.model.request.*;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.CoachSearchDto;
import com.elabbasy.coatchinghub.service.CoachService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mobile/api/coaches")
@RequiredArgsConstructor
public class MobileCoachController {

    private final CoachService coachService;

    @Operation(summary = "register step 1", description = "register step 1")
    @PostMapping("/register")
    public ApiResponse<CoachDto> step1(@RequestBody @Valid CreateCoachStep1 request) {
        return new ApiResponse<>(coachService.step1(request));
    }

    @Operation(summary = "search coaches", description = "search coaches")
    @PostMapping("/search")
    public ApiResponse<List<CoachSearchDto>> search(@RequestBody ApiRequest<CoachSearchCriteria> request) {
        return coachService.search(request);
    }

    @Operation(summary = "get coach details", description = "get coach details")
    @GetMapping("/details/{id}")
    public ApiResponse<CoachDto> getDetails(@PathVariable Long id) {
        return new ApiResponse<>(coachService.getDetails(id));
    }

    @Operation(summary = "get coach details", description = "get coach details")
    @GetMapping("/profile")
    public ApiResponse<CoachDto> getProfile(@RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId) {
        return new ApiResponse<>(coachService.getDetails(coachId));
    }
}
