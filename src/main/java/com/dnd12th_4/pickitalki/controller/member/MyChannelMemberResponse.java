package com.dnd12th_4.pickitalki.controller.member;

import lombok.Builder;

@Builder
public record MyChannelMemberResponse(Long channelMemberId, String channelId, String codeName, String channelName,
                                      String profileImage) {
}