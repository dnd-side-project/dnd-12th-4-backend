package com.dnd12th_4.pickitalki.controller.member;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums;
import com.dnd12th_4.pickitalki.controller.member.dto.ChannelFriendResponse;
import com.dnd12th_4.pickitalki.controller.member.dto.MemberResponse;
import com.dnd12th_4.pickitalki.controller.member.dto.MyChannelMemberResponse;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.service.login.MemberService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.INVITEDALL;
import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.MADEALL;
import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.SHOWALL;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/name")
    public Api<String> registerName(
            @MemberId Long memberId,
            @RequestParam("name") @NotBlank String name
    ) {
        Member member = memberService.updateName(memberId, name);

        return Api.OK(member.getNickName());
    }

    @GetMapping
    public Api<MemberResponse> findMemberInfo(
            @MemberId Long memberId
    ) {
        MemberResponse memberResponse = memberService.findMemberById(memberId);

        return Api.OK(memberResponse);
    }

    @GetMapping("/channel-members")
    public Api<List<MyChannelMemberResponse>> findMyChannelMemberInfo(
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

        List<MyChannelMemberResponse> allParticipateMyInfo = memberService.findAllChannelMyInfo(memberId, channelEnum);

        return Api.OK(allParticipateMyInfo);
    }

    @GetMapping("/friends")
    public Api<List<ChannelFriendResponse>> findMyFriends(
            @MemberId Long memberId
    ) {
        List<ChannelFriendResponse> channelFriends = memberService.findChannelFriends(memberId);

        return Api.OK(channelFriends);
    }

}
