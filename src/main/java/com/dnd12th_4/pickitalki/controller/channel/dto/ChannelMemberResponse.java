package com.dnd12th_4.pickitalki.controller.channel.dto;

import lombok.Builder;

@Builder
public record ChannelMemberResponse(long channelMemberId, String nickName, String profileImageUrl) {
}
