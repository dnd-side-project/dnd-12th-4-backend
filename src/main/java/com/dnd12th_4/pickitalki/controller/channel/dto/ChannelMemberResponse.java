package com.dnd12th_4.pickitalki.controller.channel.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ChannelMemberResponse(long memberCount, List<ChannelMemberDto> channelMembers) {
}
