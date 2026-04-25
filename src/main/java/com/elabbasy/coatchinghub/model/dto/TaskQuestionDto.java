package com.elabbasy.coatchinghub.model.dto;

import com.elabbasy.coatchinghub.model.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskQuestionDto extends AuditBaseDto {

    private String questionText;
    private QuestionType type;
    private Boolean required;
    private Integer orderIndex;

    private Set<TaskQuestionOptionDto> options;
}