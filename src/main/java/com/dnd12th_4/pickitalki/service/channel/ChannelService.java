package com.dnd12th_4.pickitalki.service.channel;

import com.dnd12th_4.pickitalki.domain.channel.*;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.member.MemberRepository;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;
    private final ChannelMemberRepository channelMemberRepository;


    @Transactional
    public Channel save(Long memberId, String channelName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST,"Channel save 24번쭐 에러발생"));

        Channel channel = new Channel(channelName);

        ChannelMember ChannelMemberEntity = getChannelMember();

        ChannelMemberEntity.makeChannel(channel);
        ChannelMemberEntity.makeMember(member);

        channelMemberRepository.save(ChannelMemberEntity);

        return channel;

    }



    private  ChannelMember getChannelMember() {
        ChannelMember ChannelMemberEntity = ChannelMember.builder()
                .role(Role.OWNER)
                .build();
        return ChannelMemberEntity;
    }
}
