package com.dnd12th_4.pickitalki.controller.question.dto;

import lombok.Builder;

@Builder
public record TodayQuestionResponse(long signalCount, boolean isExist, String content, String writer, String time) {
}
