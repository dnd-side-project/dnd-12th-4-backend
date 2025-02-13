package com.dnd12th_4.pickitalki.controller.channel;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelMemberDeleteRequest;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelMemberUpdateRequest;
import com.dnd12th_4.pickitalki.controller.channel.dto.InviteRequest;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelJoinResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelMemberResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.MemberCodeNameResponse;
import com.dnd12th_4.pickitalki.controller.member.dto.MyChannelMemberResponse;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.service.channel.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelMemberController {

    private final ChannelService channelService;

    @PatchMapping("/{channelId}/codeName")
    public Api<MemberCodeNameResponse> updateMemberCodeName(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId,
            @RequestParam("codeName") @Valid String codeName
    ) {
        MemberCodeNameResponse memberCodeNameResponse = channelService.updateCodeName(memberId, channelId, codeName);

        return Api.OK(memberCodeNameResponse);
    }

    @GetMapping("/{channelId}/members")
    public Api<ChannelMemberResponse> findChannelMembers(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId
    ) {
        ChannelMemberResponse channelMemberResponse = channelService.findChannelMembers(memberId, channelId);

        return Api.OK(channelMemberResponse);
    }

    @PatchMapping("/{channelId}/members/profile")
    public Api<MyChannelMemberResponse> updateChannelMemberProfile(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId,
            @RequestBody ChannelMemberUpdateRequest request
    ) {
        MyChannelMemberResponse myChannelMemberResponse = channelService.updateChannelMemberProfile(memberId, channelId, request.codeName(), request.image());

        return Api.OK(myChannelMemberResponse);
    }


    @PostMapping("/join")
    public Api<ChannelJoinResponse> joinMemberToChannel(
            @MemberId Long memberId,
            @RequestBody InviteRequest joinRequest
    ) {
        ChannelJoinResponse channelJoinResponse = channelService.joinMember(memberId, joinRequest.inviteCode(), joinRequest.codeName());

        return Api.OK(channelJoinResponse);
    }

    @DeleteMapping("/members")
    public ResponseEntity<String> leaveChannels(
            @MemberId Long memberId,
            @RequestBody ChannelMemberDeleteRequest request
    ) {
        channelService.leaveChannels(memberId, request.channelIds());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{channelId}/members")
    public ResponseEntity<String> leaveOneChannel(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId
    ) {
        channelService.leaveChannel(memberId, channelId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

