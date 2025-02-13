package com.dnd12th_4.pickitalki.controller.channel.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelShowAllResponse {

    private String channelId;
    private String channelRoomName;
    private String channelOwnerName;
    private Long countPerson;
    private Long signalCount;
    private String inviteCode;
    private String createdAt;
}
