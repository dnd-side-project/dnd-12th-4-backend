package com.dnd12th_4.pickitalki.controller.member.dto;

import com.dnd12th_4.pickitalki.common.dto.response.PageParamResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyChannelMemberShowAllResponse {

    private List<MyChannelMemberResponse> myChannelMemberResponse;

    private PageParamResponse pageParamResponse;
}
