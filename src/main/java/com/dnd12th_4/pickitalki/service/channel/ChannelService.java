package com.dnd12th_4.pickitalki.service.channel;

import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelControllerEnums;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelShowAllResponse;
import com.dnd12th_4.pickitalki.domain.channel.*;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.member.MemberRepository;
import com.dnd12th_4.pickitalki.domain.question.QuestionRepository;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final QuestionRepository questionRepository;


    @Transactional
    public ChannelMember save(Long memberId, String channelName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "Channel save 24번쭐 에러발생"));

        Channel channel = new Channel(channelName);

        ChannelMember ChannelMemberEntity = getChannelMember(Role.OWNER);

        ChannelMemberEntity.makeChannel(channel);
        ChannelMemberEntity.makeMember(member);

        return channelMemberRepository.save(ChannelMemberEntity);

    }

    @Transactional
    public Channel updateCodeName(Long channelMemberId, String codeName) {

        ChannelMember channelMember = channelMemberRepository.findById(channelMemberId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "ChannelMember updateCodeName 43번째 줄에서 에러 발생"));

        channelMember.setMemberCodeName(codeName);
        return channelMember.getChannel();
    }

    @Transactional
    public ChannelMember invited(Long memberId, UUID channelUuid) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "ChannelMember invided 53번째줄 에러"));
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "ChannelMember invided 55번째줄 에러"));

        validatedDuplicate(channel, member);

        ChannelMember channelMemberEntity = getChannelMember(Role.MEMBER);

        channelMemberEntity.makeMember(member);
        channelMemberEntity.makeChannel(channel);

        return channelMemberRepository.save(channelMemberEntity);

    }

    @Transactional
    public List<ChannelShowAllResponse> myRooms(Long memberId, ChannelControllerEnums status) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "ChannelMember invided 71번째줄 에러"));

        List<ChannelShowAllResponse> myRoomList = new ArrayList<>();

        member.getChannelMembers().stream()
                .filter(channelMember ->
                        status == ChannelControllerEnums.SHOWALL ||
                                (status == ChannelControllerEnums.INVITEDALL && channelMember.getRole() == Role.MEMBER) ||
                                (status == ChannelControllerEnums.MADEALL && channelMember.getRole() == Role.OWNER)
                )
                .map(this::buildChannelShowAllResponse)
                .forEach(myRoomList::add);

        return myRoomList;

    }

    private ChannelShowAllResponse buildChannelShowAllResponse(ChannelMember channelMember) {

        Channel channel = channelMember.getChannel();

        String ownerName = channel.getChannelMembers().stream()
                .filter(it -> it.getRole() == Role.OWNER && it.getChannel().equals(channel))
                .findAny()
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "buildChannelShowAllResponse DB 튜플이 없습니다"))
                .getMemberCodeName();

        long signalCount = questionRepository.countByChannelUuid(channel.getUuid());

        return ChannelShowAllResponse.builder()
                .channelOwnerName(ownerName)
                .channelRoomName(channel.getName())
                .countPerson((long) channel.getChannelMembers().size())
                .singalCount(signalCount)
                .build();
    }

    private void validatedDuplicate(Channel channel, Member member) {

        channel.getChannelMembers().forEach(channelMember -> {
            if (channelMember.getMember().getId().equals(member.getId())) {
                throw new ApiException(ErrorCode.DUPLICATED_MEMBER, "validateDuplicate 71번째줄 에러 ");
            }
        });
    }

    private ChannelMember getChannelMember(Role role) {
        return ChannelMember.builder()
                .role(role)
                .build();
    }


}
