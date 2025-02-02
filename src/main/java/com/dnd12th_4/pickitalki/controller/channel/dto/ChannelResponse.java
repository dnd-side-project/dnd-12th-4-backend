package com.dnd12th_4.pickitalki.controller.channel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelResponse {

    private UUID chanelUuid;
}
