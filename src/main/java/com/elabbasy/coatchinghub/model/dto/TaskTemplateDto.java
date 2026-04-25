package com.elabbasy.coatchinghub.model.dto;

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
public class TaskTemplateDto extends AuditBaseDto {

    private String title;
    private String description;
    private Boolean active;
    private CoachDto coach;

    private Set<TaskQuestionDto> questions;
}