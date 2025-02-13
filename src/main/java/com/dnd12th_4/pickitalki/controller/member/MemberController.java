package com.dnd12th_4.pickitalki.controller.member;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.common.dto.request.PageParamRequest;
import com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums;
import com.dnd12th_4.pickitalki.controller.member.dto.*;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.service.login.MemberService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.converters.ModelConverterRegistrar;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.INVITEDALL;
import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.MADEALL;
import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.SHOWALL;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final ModelConverterRegistrar modelConverterRegistrar;

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
    public Api<MyChannelMemberShowAllResponse> findMyChannelMemberInfo(
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

        MyChannelMemberShowAllResponse myChannelMemberShowAllResponse = memberService.findAllChannelMyInfo(memberId, channelEnum, pageParamRequest);

        return Api.OK(myChannelMemberShowAllResponse);
    }

    @GetMapping("/friends")
    public Api<ChannelFriendShowAllResponse> findMyFriends(
            @ModelAttribute PageParamRequest pageParamRequest,
            @MemberId Long memberId
    ) {
        ChannelFriendShowAllResponse channelFriendShowAllResponse = memberService.findChannelFriends(memberId, pageParamRequest);

        return Api.OK(channelFriendShowAllResponse);
    }

}
