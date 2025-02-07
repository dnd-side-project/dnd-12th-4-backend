package com.dnd12th_4.pickitalki.controller.answer.dto.response;

import com.dnd12th_4.pickitalki.domain.answer.Answer;

import java.util.List;

public record AnswerResponse(Integer nowSignal, String nowAuthor, String nowContent, List<Answer> answerList) {
}
