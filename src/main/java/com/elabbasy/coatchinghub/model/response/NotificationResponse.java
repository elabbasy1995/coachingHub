package com.elabbasy.coatchinghub.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private Long id;
    private String titleEn;
    private String titleAr;
    private String bodyEn;
    private String bodyAr;
    private Boolean read;
    private LocalDateTime createdDate;
}
