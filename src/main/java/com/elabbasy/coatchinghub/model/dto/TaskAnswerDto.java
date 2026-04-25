package com.elabbasy.coatchinghub.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskAnswerDto extends AuditBaseDto {

    private String answerText;
    private TaskQuestionOptionDto selectedOption;
    private TaskQuestionDto question;
    private TaskAssignmentDto assignment;
}