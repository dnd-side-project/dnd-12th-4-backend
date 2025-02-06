package com.dnd12th_4.pickitalki.controller.question.dto;

public record QuestionCreateRequest(String content, boolean isAnonymous, String anonymousName) {
}
