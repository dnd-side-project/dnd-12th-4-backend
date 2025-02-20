package com.dnd12th_4.pickitalki.controller.question.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionOneResponse {
    private QuestionResponse questionResponse;
    private boolean hasMyAnswer;
}
