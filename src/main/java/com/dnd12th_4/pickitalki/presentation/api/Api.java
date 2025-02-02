package com.dnd12th_4.pickitalki.presentation.api;

import com.dnd12th_4.pickitalki.presentation.error.ErrorCodeIfs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Api<T> {

    private Result result;
    private T body;

    public static <T>Api<T> OK(T data){
        Api<T> api = new Api<>();
        api.result = Result.OK();
        api.body= data;
        return api;
    }

    public static Api<Object> ERROR(Result result) {
        Api<Object> api = new Api<>();
        api.result =result;
        return api;
    }

    public static Api<Object> ERROR(ErrorCodeIfs errorCodeIfs){
        Api<Object> api = new Api<>();
        api.result = Result.ERROR(errorCodeIfs);
        return api;
    }

    public static Api<Object> ERROR(ErrorCodeIfs errorCodeIfs, Throwable tx) {
        Api<Object> api = new Api<>();
        api.result = Result.ERROR(errorCodeIfs,tx);
        return api;
    }

    public static Api<Object> ERROR(ErrorCodeIfs errorCodeIfs, String description){
        var api = new Api<Object>();
        api.result = Result.ERROR(errorCodeIfs,description);
        return api;
    }

}
