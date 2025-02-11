package com.dnd12th_4.pickitalki.controller.answer.dto.response;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerQuestionDTO {

    private String codeName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String content;

}
