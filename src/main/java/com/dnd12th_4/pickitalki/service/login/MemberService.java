package com.dnd12th_4.pickitalki.service.login;


import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelControllerEnums;
import com.dnd12th_4.pickitalki.controller.member.dto.ChannelFriendResponse;
import com.dnd12th_4.pickitalki.controller.member.dto.MemberResponse;
import com.dnd12th_4.pickitalki.controller.member.dto.MyChannelMemberResponse;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMemberRepository;
import com.dnd12th_4.pickitalki.domain.channel.Role;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.member.MemberRepository;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ChannelMemberRepository channelMemberRepository;

    @Transactional(readOnly = true)
    public MemberResponse findMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 회원정보를 조회할 수 없습니다."));

        return MemberResponse.builder()
                .name(member.getNickName())
                .email(member.getEmail())
                .profileImage(member.getProfileImageUrl())
                .build();
    }

    @Transactional(readOnly = true)
    public List<MyChannelMemberResponse> findAllChannelMyInfo(Long memberId, ChannelControllerEnums status) {
        List<ChannelMember> myChannelMembers = channelMemberRepository.findByMemberId(memberId);

        return myChannelMembers.stream()
                .filter(channelMember ->
                        status == ChannelControllerEnums.SHOWALL ||
                                (status == ChannelControllerEnums.MADEALL && channelMember.getRole() == Role.OWNER) ||
                                (status == ChannelControllerEnums.MADEALL && channelMember.getRole() == Role.MEMBER)
                )
                .map(MemberService::buildChannelMemberResponse)
                .toList();
    }

    private static MyChannelMemberResponse buildChannelMemberResponse(ChannelMember channelMember) {
        return MyChannelMemberResponse.builder()
                .channelMemberId(channelMember.getId())
                .channelName(channelMember.getChannel().getName())
                .codeName(channelMember.getMemberCodeName())
                .profileImage(channelMember.getProfileImage())
                .channelId(channelMember.getChannel().getId())
                .build();
    }

    @Transactional
    public Member updateName(Long memberId, String name) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 member가 DB에 없습니다."));

        member.setNickName(name);

        return member;
    }


    @Transactional(readOnly = true)
    public List<ChannelFriendResponse> findChannelFriends(Long memberId) {
        List<ChannelMember> meOnChannel = channelMemberRepository.findMeOnChannel(memberId);

        return meOnChannel.stream()
                .flatMap(me -> me.getChannel().getChannelMembers().stream()
                        .filter(friend -> !friend.equals(me))
                        .map(friend -> ChannelFriendResponse.builder()
                                .channelMemberId(friend.getId())
                                .channelName(friend.getChannel().getName())
                                .codeName(friend.getMemberCodeName())
                                .profileImage(friend.getProfileImage())
                                .build())
                )
                .toList();
    }
}
