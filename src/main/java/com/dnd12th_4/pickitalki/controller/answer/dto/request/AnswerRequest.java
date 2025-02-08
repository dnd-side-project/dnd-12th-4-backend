package com.dnd12th_4.pickitalki.controller.answer.dto.request;

import com.dnd12th_4.pickitalki.common.annotation.ValidAnonymousName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ValidAnonymousName
public record AnswerRequest(@NotBlank String answerForm, @NotNull boolean isAnonymous, String anonymousName) {

}
