package com.dnd12th_4.pickitalki.controller.channel;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.common.converter.UUIDConverter;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelMemberResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelResponse;
import com.dnd12th_4.pickitalki.domain.channel.Channel;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
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
    public Api<ChannelMemberResponse> makeRoom(
            @MemberId Long memberId,
            @RequestParam("channelName") @Valid String channelName
    ) {
        ChannelMember channelMember = channelService.save(memberId, channelName);
        ChannelMemberResponse channelMemberResponse = new ChannelMemberResponse(channelMember.getId());

        return Api.OK(channelMemberResponse);
    }

    @PostMapping("/codename/{channelMemberId}")
    public Api<ChannelResponse> codeName(
            @PathVariable Long channelMemberId,
            @RequestParam("codeName") @Valid String codeName
    ) {
        Channel channel = channelService.updateCodeName(channelMemberId, codeName);
        ChannelResponse channelResponse = new ChannelResponse(channel.getUuid());

        return Api.OK(channelResponse);
    }

    @PostMapping("/invited/room")
    public Api<ChannelMemberResponse> invitedRoom(
            @MemberId Long memberId,
            @RequestParam("channelUuid") @Valid String channelUuid
    ) {



        ChannelMember channelMember=channelService.invited(memberId, UUIDConverter.toUUID(channelUuid));
        ChannelMemberResponse channelMemberResponse = new ChannelMemberResponse(channelMember.getId());

        return Api.OK(channelMemberResponse);
    }
}
