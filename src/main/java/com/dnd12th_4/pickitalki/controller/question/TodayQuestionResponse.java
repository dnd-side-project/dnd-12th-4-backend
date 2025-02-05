package com.dnd12th_4.pickitalki.controller.question;

import lombok.Builder;

@Builder
public record TodayQuestionResponse(long signalCount, String content, boolean isExist, String time) {
}
