package com.dnd12th_4.pickitalki.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageParamResponse {

    private Integer currentPage;
    private Integer size;
    private Integer totalElements;
    private Integer totalPages;
    private boolean hasNext;
}
