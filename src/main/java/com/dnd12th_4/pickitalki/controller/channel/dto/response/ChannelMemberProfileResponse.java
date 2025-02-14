package com.dnd12th_4.pickitalki.controller.channel.dto.response;

import lombok.Builder;

@Builder
public record ChannelMemberProfileResponse(long channelMemberId, String codeName, String profileImageUrl, boolean isTodayQuestioner) {
}
