package com.dnd12th_4.pickitalki.controller.question.dto;


import lombok.Builder;

@Builder
public record QuestionResponse(long questionId, String writerName, long signalNumber, String content, String createdAt, long replyCount) {
}
