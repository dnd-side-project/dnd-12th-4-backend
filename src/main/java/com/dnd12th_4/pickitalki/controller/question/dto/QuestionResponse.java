package com.dnd12th_4.pickitalki.controller.question.dto;

public record QuestionResponse(String writerName, long signalNumber, String content, String createdAt) {
}
