package com.dnd12th_4.pickitalki.controller.question;

import lombok.Builder;

@Builder
public record TodayQuestionResponse(Long signalCount, String content, boolean isExist, String time) {
}
