package com.dnd12th_4.pickitalki.common.dto.request;

import jakarta.annotation.security.DenyAll;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageParamRequest {

    @NotNull
    private Integer page ;
    @NotNull
    private Integer size ;
}
