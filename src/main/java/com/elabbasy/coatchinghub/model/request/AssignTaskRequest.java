package com.elabbasy.coatchinghub.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignTaskRequest {
    private Long taskTemplateId;
    private Long bookingId;
    private LocalDate dueDate;
}