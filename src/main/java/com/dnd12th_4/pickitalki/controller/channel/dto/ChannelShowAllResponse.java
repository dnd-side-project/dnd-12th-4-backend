package com.dnd12th_4.pickitalki.controller.channel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelShowAllResponse {

    private String channelRoomName;
    private String channelOwnerName;
    private Long countPerson;
    private Long signalCount;

}
