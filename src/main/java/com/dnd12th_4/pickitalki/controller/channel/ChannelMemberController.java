package com.dnd12th_4.pickitalki.controller.channel;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.common.dto.request.PageParamRequest;
import com.dnd12th_4.pickitalki.common.pagination.Pagination;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelMemberDeleteRequest;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelMemberUpdateRequest;
import com.dnd12th_4.pickitalki.controller.channel.dto.InviteRequest;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelJoinResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelMemberProfileResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelMembersResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.MemberCodeNameResponse;
import com.dnd12th_4.pickitalki.controller.member.dto.MyChannelMemberResponse;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.service.channel.ChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Api<ChannelMembersResponse> findChannelMembers(
            @ModelAttribute PageParamRequest pageParamRequest,
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId,
            @RequestParam(value = "sort", defaultValue = "latest") String sort
    ) {
        Pageable pageable = Pagination.validateGetPage(sort, pageParamRequest);
        ChannelMembersResponse channelMembersResponse = channelService.findChannelMembers(memberId, channelId, pageable);

        return Api.OK(channelMembersResponse);
    }

    @GetMapping("/{channelId}/members/me")
    public Api<ChannelMemberProfileResponse> findMyChannelMemberProfile(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId
    ) {
        ChannelMemberProfileResponse channelMemberProfileResponse = channelService.findChannelMember(memberId, channelId);

        return Api.OK(channelMemberProfileResponse);
    }

    @GetMapping("/{channelId}/members/today-questioner")
    public Api<ChannelMemberProfileResponse> findTodayQuestionerProfile(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId
    ) {
        ChannelMemberProfileResponse todayQuestionerResponse = channelService.findTodayQuestioner(memberId, channelId);

        return Api.OK(todayQuestionerResponse);
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

