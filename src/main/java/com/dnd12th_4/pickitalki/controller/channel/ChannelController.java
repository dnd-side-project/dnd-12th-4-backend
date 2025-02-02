package com.dnd12th_4.pickitalki.controller.channel;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.domain.channel.Channel;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.service.channel.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/make/room")
    public Api<Object> makeRoom(
            @MemberId Long memberId,
            @RequestParam("channelName") @Valid String channelName
    ) {
        Channel channel = channelService.save(memberId, channelName);

        return Api.OK(channel.getUuid());
    }
}
