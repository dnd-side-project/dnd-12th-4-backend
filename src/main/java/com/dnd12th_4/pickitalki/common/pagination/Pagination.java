package com.dnd12th_4.pickitalki.common.pagination;

import com.dnd12th_4.pickitalki.common.dto.request.PageParamRequest;
import com.dnd12th_4.pickitalki.common.dto.response.PageParamResponse;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import org.springframework.data.domain.*;

import java.util.List;

public class Pagination {

    public static Pageable validateGetPage(String sort, PageParamRequest pageParamRequest) {

        int size = pageParamRequest.getSize();
        int page = pageParamRequest.getPage();
        pageParamRequest.setSize(size);
        pageParamRequest.setPage(page);

        Pageable pageable = null;
        if(sort.equals("latest")){
            pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        else if(sort.equals("oldest")){
            pageable = PageRequest.of(page,size, Sort.by(Sort.Direction.ASC, "createdAt"));
        }
        else{
            throw new ApiException(ErrorCode.BAD_REQUEST,"sort값을 확인해주세요");
        }
        return pageable;
    }


    public static <T> Page<T> getPage(Pageable pageable, List<T> list) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    public static PageParamResponse createPageParamResponse(Page<?> page) {
        return new PageParamResponse(
                page.getNumber(),
                page.getSize(),
                (int) page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
    }


}
