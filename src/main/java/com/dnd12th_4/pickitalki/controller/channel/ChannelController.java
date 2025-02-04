package com.dnd12th_4.pickitalki.controller.channel;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelControllerEnums;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelJoinResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelShowAllResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.InviteCodeDto;
import com.dnd12th_4.pickitalki.controller.channel.dto.MemberCodeNameResponse;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.service.channel.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<ChannelResponse> makeChannel(
            @MemberId Long memberId,
            @RequestParam("channelName") @Valid String channelName,
            @RequestParam(value = "codeName", required = false) String codeName
    ) {
        ChannelResponse channelResponse = channelService.save(memberId, channelName, codeName);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(channelResponse);
    }

    @PatchMapping("/{channelId}/codeName")
    public Api<MemberCodeNameResponse> updateMemberCodeName(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId,
            @RequestParam("codeName") @Valid String codeName
    ) {
        MemberCodeNameResponse memberCodeNameResponse = channelService.updateCodeName(memberId, channelId, codeName);

        return Api.OK(memberCodeNameResponse);
    }

    @GetMapping("/inviteCode")
    public ResponseEntity<InviteCodeDto> getChannelInviteCode(
            @MemberId Long memberId,
            @RequestParam("channelName") @Valid String channelName
    ) {
        String inviteCode = channelService.findInviteCode(memberId, channelName);
        return ResponseEntity.ok(new InviteCodeDto(inviteCode));
    }

    @PostMapping("/join")
    public Api<ChannelJoinResponse> joinMemberToChannel(
            @MemberId Long memberId,
            @RequestBody InviteCodeDto joinRequest,
            @RequestParam(value = "codeName", required = false) String codeName
    ) {
        ChannelJoinResponse channelJoinResponse = channelService.joinMember(memberId, joinRequest.inviteCode(), codeName);

        return Api.OK(channelJoinResponse);
    }

    @GetMapping("/invited")
    public Api<List<ChannelShowAllResponse>> findAllInvitedChannels(
            @MemberId Long memberId
    ) {
        List<ChannelShowAllResponse> channelShowAllResponses = channelService.findAllMyChannels(memberId, ChannelControllerEnums.INVITEDALL);

        return Api.OK(channelShowAllResponses);
    }

    @GetMapping("/make/room/all")
    public Api<List<ChannelShowAllResponse>> findAllOwnChannels(
            @MemberId Long memberId
    ) {
        List<ChannelShowAllResponse> channelShowAllResponses = channelService.findAllMyChannels(memberId, ChannelControllerEnums.MADEALL);

        return Api.OK(channelShowAllResponses);
    }

    @GetMapping
    public Api<List<ChannelShowAllResponse>> findAllChannels(
            @MemberId Long memberId
    ) {
        List<ChannelShowAllResponse> channelShowAllResponses = channelService.findAllMyChannels(memberId, ChannelControllerEnums.SHOWALL);
        return Api.OK(channelShowAllResponses);
    }
}




