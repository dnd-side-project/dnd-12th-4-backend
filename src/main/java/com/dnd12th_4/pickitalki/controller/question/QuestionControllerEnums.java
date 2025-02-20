package com.dnd12th_4.pickitalki.controller.question;

public enum QuestionControllerEnums {
    ALL,
    MY_QUESTIONS,
    OTHERS;

    public static QuestionControllerEnums from(String filter) {
        return switch (filter) {
            case "all" -> ALL;
            case "my-questions", "my-signal" -> MY_QUESTIONS;
            case "others", "friend-signal" -> OTHERS;
            default -> throw new IllegalArgumentException("지원하지 않는 questionFilter 값입니다.");
        };
    }
}
