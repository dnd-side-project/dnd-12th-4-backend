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
public class AnswerResponse {

    private String createdAt;
    private String updatedAt;

    private String writerProfileImage;
    private Long id;
    private String content;
    private boolean isMyAnswer;
    private String codeName;

}
