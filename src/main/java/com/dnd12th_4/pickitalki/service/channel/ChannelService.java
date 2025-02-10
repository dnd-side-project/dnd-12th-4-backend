package com.dnd12th_4.pickitalki.service.channel;

import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelControllerEnums;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelJoinResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelMemberDto;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelShowAllResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelSpecificResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.MemberCodeNameResponse;
import com.dnd12th_4.pickitalki.domain.channel.Channel;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMemberRepository;
import com.dnd12th_4.pickitalki.domain.channel.ChannelRepository;
import com.dnd12th_4.pickitalki.domain.channel.Role;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.member.MemberRepository;
import com.dnd12th_4.pickitalki.domain.question.QuestionRepository;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.micrometer.common.util.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public ChannelResponse save(Long memberId, String channelName, String codeName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "존재하지 않는 회원입니다."));

        Channel channel = new Channel(channelName);
        ChannelMember channelMember;
        if (isBlank(codeName)) {
            channelMember = new ChannelMember(channel, member, member.getNickName(), Role.OWNER);
        } else {
            channelMember = new ChannelMember(channel, member, codeName, Role.OWNER);
        }

        channel.joinChannelMember(channelMember);
        channel = channelRepository.save(channel);

        return new ChannelResponse(channel.getUuid().toString(), channelName, channel.getInviteCode());
    }

    @Transactional
    public MemberCodeNameResponse updateCodeName(Long memberId, String channelId, String codeName) {
        Channel channel = channelRepository.findByUuid(UUID.fromString(channelId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다. 코드네임을 변경할 수 없습니다."));

        ChannelMember channelMember = channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("채널에 해당 회원이 존재하지 않습니다. 코드네임을 변경할 수 없습니다."));
        channelMember.setMemberCodeName(codeName);

        return new MemberCodeNameResponse(codeName);
    }

    @Transactional
    public ChannelJoinResponse joinMember(Long memberId, String inviteCode, String codeName) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "존재하지 않는 회원입니다. 채널에 참여할 수 없습니다."));

        Channel channel = channelRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 초대코드에 맞는 채널을 찾을 수 없습니다."));

        ChannelMember channelMember;
        if (isBlank(codeName)) {
            channelMember = new ChannelMember(channel, member, member.getNickName(), Role.MEMBER);
        } else {
            channelMember = new ChannelMember(channel, member, codeName, Role.MEMBER);
        }

        channel.joinChannelMember(channelMember);
        channelMember = channelMemberRepository.save(channelMember);

        return new ChannelJoinResponse(channel.getId(), channel.getName(), channelMember.getMemberCodeName());
    }

    @Transactional
    public List<ChannelShowAllResponse> findAllMyChannels(Long memberId, ChannelControllerEnums status) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "존재하지 않는 회원입니다. 참여한 채널들을 조회할 수 없습니다."));

        List<ChannelShowAllResponse> myChannels = new ArrayList<>();

        member.getChannelMembers().stream()
                .filter(channelMember ->
                        status == ChannelControllerEnums.SHOWALL ||
                                (status == ChannelControllerEnums.INVITEDALL && channelMember.getRole() == Role.MEMBER) ||
                                (status == ChannelControllerEnums.MADEALL && channelMember.getRole() == Role.OWNER)
                )
                .map(this::buildChannelShowAllResponse)
                .forEach(myChannels::add);

        return myChannels;
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
                .channelId(channel.getId())
                .channelOwnerName(ownerName)
                .channelRoomName(channel.getName())
                .countPerson((long) channel.getChannelMembers().size())
                .signalCount(signalCount)
                .build();
    }

    @Transactional(readOnly= true)
    public String findInviteCode(Long memberId, String channelName) {
        Channel channel = channelRepository.findByName(channelName)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 채널을 찾을 수 없습니다. 초대코드를 응답할 수 없습니다."));

       channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("채널에 해당 회원이 존재하지 않습니다. 초대코드를 열람할 권한이 없습니다."));
        return channel.getInviteCode();
    }

    @Transactional(readOnly= true)
    public ChannelSpecificResponse findChannelByChannelName(Long memberId, String channelName) {
        Channel channel = channelRepository.findByName(channelName)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 채널을 찾을 수 없습니다. 채널정보를 응답할 수 없습니다."));
        channel.findChannelMemberById(memberId);

        String ownerName = channel.getChannelMembers().stream()
                .filter(it -> it.getRole() == Role.OWNER && it.getChannel().equals(channel))
                .findAny()
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 채널의 주인을 찾을 수 없습니다."))
                .getMemberCodeName();

        long signalCount = questionRepository.countByChannelUuid(channel.getUuid());

        return ChannelSpecificResponse.builder()
                .channelId(channel.getId())
                .channelOwnerName(ownerName)
                .channelRoomName(channel.getName())
                .countPerson((long) channel.getChannelMembers().size())
                .signalCount(signalCount)
                .build();
    }

    @Transactional(readOnly= true)
    public List<ChannelMemberDto> findChannelMembers(Long memberId, String channelId) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널을 찾을 수 없습니다. 채널의 회원정보를 응답할 수 없습니다."));
        channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("채널에 해당 회원이 존재하지 않습니다. 채널의 회원정보들을 조회할 권한이 없습니다."));

        return channel.getChannelMembers()
                .stream().map(cm -> ChannelMemberDto.builder()
                        .nickName(cm.getMemberCodeName())
                        .profileImageUrl(cm.getMember().getProfileImageUrl())
                        .channelMemberId(cm.getId())
                        .build()
                ).toList();
    }

    @Transactional(readOnly = true)
    public ChannelSpecificResponse findChannelByChannelId(Long memberId, String channelId) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 이름의 채널을 찾을 수 없습니다. 채널정보를 응답할 수 없습니다."));
        channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널에 해당 멤버가 참여해 있지 않습니다. 채널정보를 조회할 권한이 없습니다."));

        String ownerName = channel.getChannelMembers().stream()
                .filter(it -> it.getRole() == Role.OWNER && it.getChannel().equals(channel))
                .findAny()
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 채널의 주인을 찾을 수 없습니다."))
                .getMemberCodeName();

        long signalCount = questionRepository.countByChannelUuid(channel.getUuid());

        return ChannelSpecificResponse.builder()
                .channelId(channelId)
                .channelOwnerName(ownerName)
                .channelRoomName(channel.getName())
                .countPerson((long) channel.getChannelMembers().size())
                .signalCount(signalCount)
                .build();

    }
}
