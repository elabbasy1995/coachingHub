package com.elabbasy.coatchinghub.model.response;

import com.elabbasy.coatchinghub.model.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TaskAssignmentDetailsResponse {

    private Long assignmentId;
    private String status;
    private LocalDate dueDate;

    private Long templateId;
    private String title;
    private String description;

    private List<TaskQuestionDetails> questions;

    @Getter
    @Setter
    public static class TaskQuestionDetails {

        private Long id;
        private String title;
        private QuestionType type;
        private Boolean required;

        private List<TaskQuestionOptionDetails> options;

        // Answer fields
        private String answerText;
        private Long selectedOptionId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskQuestionOptionDetails {
        private Long id;
        private String optionText;
    }
}