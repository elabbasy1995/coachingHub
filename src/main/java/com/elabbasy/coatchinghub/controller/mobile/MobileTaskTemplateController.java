package com.elabbasy.coatchinghub.controller.mobile;

import com.elabbasy.coatchinghub.constant.Constants;
import com.elabbasy.coatchinghub.model.dto.TaskAssignmentDto;
import com.elabbasy.coatchinghub.model.request.AssignTaskRequest;
import com.elabbasy.coatchinghub.model.request.CreateTaskWithQuestionsRequest;
import com.elabbasy.coatchinghub.model.request.SubmitTaskAnswersRequest;
import com.elabbasy.coatchinghub.model.response.*;
import com.elabbasy.coatchinghub.service.TaskTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mobile/api/task-template")
@RequiredArgsConstructor
public class MobileTaskTemplateController {

    private final TaskTemplateService taskTemplateService;

    @PostMapping
    public ApiResponse<TaskTemplateResponse> createTask(@RequestBody @Valid CreateTaskWithQuestionsRequest createTaskWithQuestionsRequest,
                                                        @RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId) {
        TaskTemplateResponse taskWithQuestions = taskTemplateService.createTaskWithQuestions(coachId, createTaskWithQuestionsRequest);

        return new ApiResponse<>(taskWithQuestions);
    }

    @GetMapping("/get-for-coach")
    public ApiResponse<List<TaskTemplateResponse>> getForCoach(@RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId,
                                                               @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                                               @RequestParam(name = "pageSize", defaultValue = Integer.MAX_VALUE + "") Integer pageSize) {
        return taskTemplateService.getTaskTemplates(coachId, pageIndex, pageSize);
    }

    @PostMapping("/assign-to-booking")
    public ApiResponse<TaskAssignmentDto> assignToBooking(@RequestBody AssignTaskRequest assignTaskRequest,
                                                          @RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId) {
        return new ApiResponse<>(taskTemplateService.assignTaskToBooking(coachId, assignTaskRequest));
    }

    @PostMapping("/submit-answer")
    public ApiResponse<TaskSubmissionResponse> submitAnswers(
            @RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId,
            @RequestBody SubmitTaskAnswersRequest request
    ) {
        return new ApiResponse<>(taskTemplateService.submitAnswers(coacheeId, request));
    }

    @GetMapping("/get-for-coachee/{coacheeId}")
    public ApiResponse<List<TaskAssignmentSummaryResponse>> getForCoacheeWithId(@PathVariable Long coacheeId,
                                                                        @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                                                        @RequestParam(name = "pageSize", defaultValue = Integer.MAX_VALUE + "") Integer pageSize) {
        return taskTemplateService.getByCoachee(coacheeId, pageSize, pageIndex);
    }

    @GetMapping("/get-by-booking/{bookingId}")
    public ApiResponse<List<TaskAssignmentSummaryResponse>> getByBooking(@PathVariable Long bookingId,
                                                                                @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                                                                @RequestParam(name = "pageSize", defaultValue = Integer.MAX_VALUE + "") Integer pageSize) {
        return taskTemplateService.getByBooking(bookingId, pageSize, pageIndex);
    }

    @GetMapping("/get-for-coachee")
    public ApiResponse<List<TaskAssignmentSummaryResponse>> getForCoachee(@RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId,
                                                                                @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
                                                                                @RequestParam(name = "pageSize", defaultValue = Integer.MAX_VALUE + "") Integer pageSize) {
        return taskTemplateService.getByCoachee(coacheeId, pageSize, pageIndex);
    }

    @GetMapping("/get-task-assignment-details/{assignmentId}")
    public ApiResponse<TaskAssignmentDetailsResponse> getAssignmentDetails(@PathVariable Long assignmentId) {
        return new ApiResponse<>(taskTemplateService.getDetails(assignmentId));
    }
}
