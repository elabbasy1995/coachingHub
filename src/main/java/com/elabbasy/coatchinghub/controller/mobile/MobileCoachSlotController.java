package com.elabbasy.coatchinghub.controller.mobile;

import com.elabbasy.coatchinghub.constant.Constants;
import com.elabbasy.coatchinghub.model.dto.CoachSlotDto;
import com.elabbasy.coatchinghub.model.enums.SlotType;
import com.elabbasy.coatchinghub.model.request.CreateCoachSlot;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.DaySlotsDto;
import com.elabbasy.coatchinghub.model.response.SlotTypeResponse;
import com.elabbasy.coatchinghub.service.CoachSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/mobile/api/coach-slot")
@RequiredArgsConstructor
@Tag(name = "Coach-Slot")
public class MobileCoachSlotController {

    private final CoachSlotService coachSlotService;

    @Operation(summary = "create coach slot", description = "create coach slot")
    @PostMapping
    public ApiResponse<CoachSlotDto> create(@RequestBody @Valid CreateCoachSlot createCoachSlot,
                                            @RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId) {
        return new ApiResponse<>(coachSlotService.create(createCoachSlot, coachId));
    }
    @Operation(summary = "delete coach", description = "delete coach")
    @DeleteMapping("/{id}")
    public ApiResponse<?> delete(@PathVariable Long id,
                                 @RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId) {
        coachSlotService.delete(id, coachId);

        return new ApiResponse<>(HttpStatus.OK);
    }

    @GetMapping
    public ApiResponse<List<DaySlotsDto>> getCoachSlots(@RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId){
        return new ApiResponse<>(coachSlotService.getSlotsByDay(coachId));
    }

    @GetMapping("/by-month-and-year")
    public ApiResponse<List<DaySlotsDto>> getCoachSlotsByMonthAndYear(@RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId,
                                                                      @RequestParam Integer month, @RequestParam Integer year){
        return new ApiResponse<>(coachSlotService.getSlotsByMonthAndYear(coachId, month, year));
    }

    @GetMapping("/available-by-month-and-year")
    public ApiResponse<List<DaySlotsDto>> getCoachAvailableSlotsByMonthAndYear(@RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId,
                                                                      @RequestParam Integer month, @RequestParam Integer year){
        return new ApiResponse<>(coachSlotService.getAvailableSlotsByMonthAndYear(coachId, month, year));
    }

    @GetMapping("/available/{coachId}")
    public ApiResponse<List<DaySlotsDto>> getAvailableCoachSlots(@PathVariable Long coachId){
        return new ApiResponse<>(coachSlotService.getAvailableSlotsByDay(coachId));
    }

    @GetMapping("/by-month-and-year/{coachId}")
    public ApiResponse<List<DaySlotsDto>> getCoachSlotsByMonthAndYearForCoachee(@PathVariable Long coachId,
                                                                      @RequestParam Integer month, @RequestParam Integer year){
        return new ApiResponse<>(coachSlotService.getSlotsByMonthAndYear(coachId, month, year));
    }

    @GetMapping("/available-by-month-and-year/{coachId}")
    public ApiResponse<List<DaySlotsDto>> getAvailableCoachSlotsByMonthAndYearForCoachee(@PathVariable Long coachId,
                                                                                @RequestParam Integer month, @RequestParam Integer year){
        return new ApiResponse<>(coachSlotService.getAvailableSlotsByMonthAndYear(coachId, month, year));
    }
}
