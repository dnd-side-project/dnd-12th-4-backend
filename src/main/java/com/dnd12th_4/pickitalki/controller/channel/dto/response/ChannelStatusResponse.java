package com.dnd12th_4.pickitalki.controller.channel.dto.response;

import lombok.Builder;

@Builder
public record ChannelStatusResponse(String channelName, String channelId, int level, int point, String characterImageUri) {
}
