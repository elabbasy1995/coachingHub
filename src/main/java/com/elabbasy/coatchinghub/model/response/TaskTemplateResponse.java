package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TaskTemplateResponse {

    private Long id;
    private String title;
    private String description;
    private Boolean active;

    // Include questions
    private List<TaskQuestionResponse> questions;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TaskQuestionResponse {

        private Long id;
        private String questionText;
        private QuestionType type;
        private Boolean required;
        private Integer orderIndex;
        private List<QuestionOptionResponse> options;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class QuestionOptionResponse {

        private Long id;
        private String optionText;
    }
}