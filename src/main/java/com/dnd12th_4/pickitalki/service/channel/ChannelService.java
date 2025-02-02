package com.dnd12th_4.pickitalki.service.channel;

import com.dnd12th_4.pickitalki.domain.channel.*;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.member.MemberRepository;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;
    private final ChannelMemberRepository channelMemberRepository;


    @Transactional
    public ChannelMember save(Long memberId, String channelName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST,"Channel save 24번쭐 에러발생"));

        Channel channel = new Channel(channelName);

        ChannelMember ChannelMemberEntity = getChannelMember();

        ChannelMemberEntity.makeChannel(channel);
        ChannelMemberEntity.makeMember(member);

        ChannelMember channelMember = channelMemberRepository.save(ChannelMemberEntity);

        return channelMember;

    }

    @Transactional
    public Channel updateCodeName(Long channelMemberId, String codeName) {

        ChannelMember channelMember = channelMemberRepository.findById(channelMemberId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "ChannelMember updateCodeName 43번째 줄에서 에러 발생"));

        channelMember.setMemberCodeName(codeName);
        return channelMember.getChannel();
    }


    private  ChannelMember getChannelMember() {
        ChannelMember ChannelMemberEntity = ChannelMember.builder()
                .role(Role.OWNER)
                .build();
        return ChannelMemberEntity;
    }


}
