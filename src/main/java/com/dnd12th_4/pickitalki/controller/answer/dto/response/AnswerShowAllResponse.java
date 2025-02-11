package com.dnd12th_4.pickitalki.controller.answer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerShowAllResponse {

    private Integer signalCount;

    private AnswerQuestionDTO questionDTO;

    private List<AnswerResponse> answerList;
}
