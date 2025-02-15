package com.dnd12th_4.pickitalki.common.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.security.DenyAll;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PageParamRequest {


    private Integer page;

    private Integer size ;

    public Integer getPage() {
        return (page != null) ? page : 0;
    }

    public Integer getSize() {
        return (size != null) ? size : 5;
    }
}
