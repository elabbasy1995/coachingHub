package com.elabbasy.coatchinghub.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskAssignmentSummaryResponse {

    private Long assignmentId;
    private Long templateId;
    private String title;
    private String description;
    private String status;
    private LocalDate dueDate;
}