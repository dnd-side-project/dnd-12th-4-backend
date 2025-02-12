package com.dnd12th_4.pickitalki.controller.channel.dto.response;

import lombok.Builder;

@Builder
public record ChannelSpecificResponse(String channelId, String channelRoomName, String channelOwnerName,
                                      Long countPerson, Long signalCount, String inviteCode) {
}
