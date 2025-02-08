package com.dnd12th_4.pickitalki.controller.channel.dto;

import lombok.Builder;

@Builder
public record ChannelSpecificResponse(String channelId, String channelRoomName, String channelOwnerName,
                                      Long countPerson, Long signalCount) {
    //응답 1개에 대한 섬네일 정보- 응답자코드네임, 응답내용, created_at 도 필요할 수도
}
