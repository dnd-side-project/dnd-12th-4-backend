package com.dnd12th_4.pickitalki.controller.question;

public record QuestionResponse(String authorName, long signalNumber, String content, String createdAt) {
}
