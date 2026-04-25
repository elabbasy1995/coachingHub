package com.elabbasy.coatchinghub.model.dto;

import com.elabbasy.coatchinghub.model.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto extends AuditBaseDto {

    private String title;
    private String body;
    private Boolean read;
    private LocalDateTime readAt;

    private Long referenceId;
    private NotificationType notificationType;

    private CoachDto coach;
    private CoacheeDto coachee;
}
