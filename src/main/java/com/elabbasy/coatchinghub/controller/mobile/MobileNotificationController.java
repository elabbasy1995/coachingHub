package com.elabbasy.coatchinghub.controller.mobile;

import com.elabbasy.coatchinghub.constant.Constants;
import com.elabbasy.coatchinghub.model.response.ApiResponse;
import com.elabbasy.coatchinghub.model.response.NotificationResponse;
import com.elabbasy.coatchinghub.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mobile/api/notifications")
@RequiredArgsConstructor
public class MobileNotificationController {

    private final NotificationService notificationService;

    @GetMapping("/coach")
    public ApiResponse<List<NotificationResponse>> getCoachNotifications(
            @RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId,
            @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", defaultValue = Integer.MAX_VALUE + "") Integer pageSize
    ) {
        return notificationService.getCoachNotifications(coachId, pageIndex, pageSize);
    }

    @GetMapping("/coachee")
    public ApiResponse<List<NotificationResponse>> getCoacheeNotifications(
            @RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId,
            @RequestParam(name = "pageIndex", defaultValue = "0") Integer pageIndex,
            @RequestParam(name = "pageSize", defaultValue = Integer.MAX_VALUE + "") Integer pageSize
    ) {
        return notificationService.getCoacheeNotifications(coacheeId, pageIndex, pageSize);
    }

    @PutMapping("/read/{notificationId}")
    public ApiResponse<String> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return new ApiResponse<>("success");
    }

    @PutMapping("/coach/read-all")
    public ApiResponse<String> markAllCoachAsRead(
            @RequestAttribute(name = Constants.COACH_ID_ATTRIBUTE) Long coachId
    ) {
        notificationService.markAllCoachAsRead(coachId);
        return new ApiResponse<>("success");
    }

    @PutMapping("/coachee/read-all")
    public ApiResponse<String> markAllCoacheeAsRead(
            @RequestAttribute(name = Constants.COACHEE_ID_ATTRIBUTE) Long coacheeId
    ) {
        notificationService.markAllCoacheeAsRead(coacheeId);
        return new ApiResponse<>("success");
    }
}