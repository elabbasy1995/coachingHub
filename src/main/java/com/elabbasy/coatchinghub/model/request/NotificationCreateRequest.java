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

    private String titleEn;
    private String titleAr;
    private String messageEn;
    private String messageAr;

    private NotificationType notificationType;

    private Long referenceId;
}
