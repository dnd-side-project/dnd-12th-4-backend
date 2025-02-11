package com.dnd12th_4.pickitalki.controller.channel.dto.response;

import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelMemberDto;
import lombok.Builder;

import java.util.List;

@Builder
public record ChannelMemberResponse(long memberCount, List<ChannelMemberDto> channelMembers) {
}
