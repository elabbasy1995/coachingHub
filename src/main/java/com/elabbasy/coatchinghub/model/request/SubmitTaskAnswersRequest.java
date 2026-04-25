package com.elabbasy.coatchinghub.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitTaskAnswersRequest {

    private Long assignmentId;

    private List<QuestionAnswerRequest> answers;

    @Getter
    @Setter
    public static class QuestionAnswerRequest {
        private Long questionId;

        // Used for TEXT / NUMBER / etc
        private String answerText;

        // Used for single choice
        private Long selectedOptionId;

    }
}