package com.dnd12th_4.pickitalki.controller.member.dto;

import lombok.Builder;

@Builder
public record ChannelFriendResponse(Long channelMemberId, String channelName, String codeName, String profileImage) {
}
