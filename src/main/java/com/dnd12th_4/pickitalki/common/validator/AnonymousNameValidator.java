package com.dnd12th_4.pickitalki.common.validator;

import com.dnd12th_4.pickitalki.common.annotation.ValidAnonymousName;
import com.dnd12th_4.pickitalki.controller.answer.dto.request.AnswerRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AnonymousNameValidator implements ConstraintValidator<ValidAnonymousName, AnswerRequest> {
    @Override
    public boolean isValid(AnswerRequest request, ConstraintValidatorContext context) {
        if (request.isAnonymous() && (request.anonymousName() == null || request.anonymousName().isBlank())) {
            return false;
        }
        return true;
    }
}