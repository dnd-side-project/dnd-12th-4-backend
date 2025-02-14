package com.dnd12th_4.pickitalki.controller.channel.dto.response;

import com.dnd12th_4.pickitalki.common.dto.response.PageParamResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelMemberDto;
import lombok.Builder;

import java.util.List;

@Builder
public record ChannelMemberResponse(String channelName, long memberCount, List<ChannelMemberDto> channelMembers, PageParamResponse pageParamResponse) {

}
