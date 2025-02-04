package com.dnd12th_4.pickitalki.controller.channel.dto;

import jakarta.validation.constraints.NotBlank;

public record ChannelResponse(@NotBlank String channelId, String channelName, String inviteCode) {
}
