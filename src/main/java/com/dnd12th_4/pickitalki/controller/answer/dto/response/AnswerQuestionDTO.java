package com.dnd12th_4.pickitalki.controller.answer.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerQuestionDTO {

    private String codeName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String writerProfileImage;
    private String content;

}
