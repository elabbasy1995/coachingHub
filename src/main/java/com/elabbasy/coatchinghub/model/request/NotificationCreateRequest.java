package com.elabbasy.coatchinghub.model.request;

import com.elabbasy.coatchinghub.model.enums.NotificationType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateRequest {

    private Long coachId;
    private Long coacheeId;

    private String title;
    private String message;

    private NotificationType notificationType;

    private Long referenceId;
}