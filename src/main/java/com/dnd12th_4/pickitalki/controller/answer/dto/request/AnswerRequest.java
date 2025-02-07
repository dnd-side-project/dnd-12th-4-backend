package com.dnd12th_4.pickitalki.controller.answer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnswerRequest(@NotBlank String answerForm, @NotNull boolean isAnonymous, String anonymousName) {

}
