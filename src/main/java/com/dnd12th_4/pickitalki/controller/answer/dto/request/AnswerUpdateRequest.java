package com.dnd12th_4.pickitalki.controller.answer.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerUpdateRequest {

    @NotNull
    private String content;

}
