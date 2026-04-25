package com.elabbasy.coatchinghub.model.request;

import com.elabbasy.coatchinghub.model.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateTaskWithQuestionsRequest {

    @NotBlank(message = "TITLE_IS_REQUIRED")
    private String title;
    private String description;

    @Valid
    @NotNull(message = "QUESTIONS_IS_REQUIRED")
    @NotEmpty(message = "QUESTIONS_IS_REQUIRED")
    private List<TaskQuestionItem> questions;

    @Getter
    @Setter
    public static class TaskQuestionItem {
        @NotBlank(message = "QUESTION_TEXT_IS_REQUIRED")
        private String questionText;
        @NotNull(message = "QUESTION_TYPE_IS_REQUIRED")
        private QuestionType type;
        private Boolean required = false;
        @NotNull(message = "QUESTION_INDEX_IS_REQUIRED")
        private Integer orderIndex;
        @Valid
        private List<TaskQuestionOptionItem> options; // only for MULTIPLE_CHOICE
    }

    @Getter
    @Setter
    public static class TaskQuestionOptionItem {
        @NotBlank(message = "OPTION_TEXT_IS_REQUIRED")
        private String optionText;
    }
}