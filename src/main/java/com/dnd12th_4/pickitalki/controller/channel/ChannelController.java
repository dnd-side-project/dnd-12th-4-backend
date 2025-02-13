package com.dnd12th_4.pickitalki.controller.channel;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.common.dto.request.PageParamRequest;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelCreateRequest;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.*;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelMemberDto;

import com.dnd12th_4.pickitalki.controller.channel.dto.InviteCodeDto;
import com.dnd12th_4.pickitalki.controller.channel.dto.InviteRequest;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.service.channel.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.INVITEDALL;
import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.MADEALL;
import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.SHOWALL;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping
    public ResponseEntity<ChannelResponse> makeChannel(
            @MemberId Long memberId,
            @RequestBody ChannelCreateRequest channelCreateRequest
    ) {
        ChannelResponse channelResponse = channelService.save(memberId,
                channelCreateRequest.channelName(), channelCreateRequest.codeName());

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

    @GetMapping("/{channelId}/members")
    public Api<ChannelMemberResponse> findChannelMembers(
            @ModelAttribute PageParamRequest pageParamRequest,
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId
    ) {
        ChannelMemberResponse channelMemberResponse = channelService.findChannelMembers(memberId, channelId, pageParamRequest);

        return Api.OK(channelMemberResponse);
//                .memberCount(channelMembers.size())
//                .channelMembers(channelMembers)
//                .build()

    }

    @GetMapping("/{channelId}/members/status")
    public Api<ChannelMemberStatusResponse> findChannelMemberStatus(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId
    ) {
        ChannelMemberStatusResponse channelMemberStatus = channelService.findChannelMemberStatus(memberId, channelId);

        return Api.OK(channelMemberStatus);
    }


    @GetMapping("/inviteCode")
    public ResponseEntity<InviteCodeDto> findChannelInviteCode(
            @MemberId Long memberId,
            @RequestParam("channelName") @Valid String channelName
    ) {
        String inviteCode = channelService.findInviteCode(memberId, channelName);
        return ResponseEntity.ok(new InviteCodeDto(inviteCode));
    }

    @PostMapping("/join")
    public Api<ChannelJoinResponse> joinMemberToChannel(
            @MemberId Long memberId,
            @RequestBody InviteRequest joinRequest
    ) {
        ChannelJoinResponse channelJoinResponse = channelService.joinMember(memberId, joinRequest.inviteCode(), joinRequest.codeName());

        return Api.OK(channelJoinResponse);
    }

    @GetMapping
    public Api<ChannelSpecificResponse> findChannelByName(
            @MemberId Long memberId,
            @RequestParam(value = "channelName") String channelName
    ) {
        ChannelSpecificResponse channelSpecificResponse = channelService.findChannelByChannelName(memberId, channelName);

        return Api.OK(channelSpecificResponse);
    }

    @GetMapping("/{channelId}")
    public Api<ChannelSpecificResponse> findChannelById(
            @MemberId Long memberId,
            @PathVariable(value = "channelId") String channelId
    ) {
        ChannelSpecificResponse channelSpecificResponse = channelService.findChannelByChannelId(memberId, channelId);

        return Api.OK(channelSpecificResponse);
    }

    @GetMapping("/channel-profile")
    public Api<ChannelShowAllResponse> findChannelsByRole(
            @ModelAttribute PageParamRequest pageParamRequest,
            @MemberId Long memberId,
            @RequestParam("tab") String channelFilter
    ) {
        ChannelControllerEnums channelEnum;
        if (channelFilter.equals("all")) {
            channelEnum = SHOWALL;
        } else if (channelFilter.equals("my-channel")) {
            channelEnum = MADEALL;
        } else if (channelFilter.equals("invited-channel")) {
            channelEnum = INVITEDALL;
        } else {
            throw new IllegalArgumentException("지원하지 않는 파라미터입니다. all, my-channel, invited-channel 중 1개를 요청헤주세요");
        }
        ChannelShowAllResponse channelShowAllResponse =  channelService.findAllMyChannels(memberId, channelEnum, pageParamRequest);
        return Api.OK(channelShowAllResponse);
    }

}




