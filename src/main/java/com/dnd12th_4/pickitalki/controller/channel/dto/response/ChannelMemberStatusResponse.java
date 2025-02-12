package com.dnd12th_4.pickitalki.controller.channel.dto.response;

import lombok.Builder;

@Builder
public record ChannelMemberStatusResponse(String channelName, int countPerson, long channelMemberId, String codeName,
                                          int level, int point, int todayAnswerCount, String characterImageUri) {
}
