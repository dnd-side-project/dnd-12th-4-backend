package com.dnd12th_4.pickitalki.presentation.exception;

import com.dnd12th_4.pickitalki.presentation.error.ErrorCodeIfs;

public interface ApiExceptionIfs {

    ErrorCodeIfs getErrorCodeIfs();
    String getErrorDescription();

}
