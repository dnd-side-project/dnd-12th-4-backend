package com.dnd12th_4.pickitalki.common.converter;

import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;

import java.util.UUID;

public class UUIDConverter {

    public static UUID toUUID(String uuidStr) {

        if(uuidStr==null|| uuidStr.isEmpty()){
            throw new ApiException(ErrorCode.BAD_REQUEST,"UUID convertToUUID 에러");
        }

        if (uuidStr.length() == 32) {
            // UUID 8-4-4-4-12 형식으로 변환
            uuidStr = uuidStr.replaceFirst(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                    "$1-$2-$3-$4-$5"
            );
        }
        return UUID.fromString(uuidStr);
    }
}
