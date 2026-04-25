package com.elabbasy.coatchinghub.model.dto;

import com.elabbasy.coatchinghub.model.enums.TaskAssignmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskAssignmentDto extends AuditBaseDto {

    private TaskAssignmentStatus status;
    private LocalDate dueDate;

    private BookingDto booking; // include basic booking info
    private CoacheeDto coachee; // include coachee info
    private TaskTemplateDto template; // nested template with questions
}