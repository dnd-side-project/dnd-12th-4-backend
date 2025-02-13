package com.dnd12th_4.pickitalki.service.channel;

import com.dnd12th_4.pickitalki.common.config.AppConfig;
import com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelMemberDto;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelJoinResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelMemberResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelStatusResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelShowAllResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelSpecificResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.MemberCodeNameResponse;
import com.dnd12th_4.pickitalki.controller.member.dto.MyChannelMemberResponse;
import com.dnd12th_4.pickitalki.domain.channel.Channel;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
import com.dnd12th_4.pickitalki.domain.channel.ChannelLevel;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMemberRepository;
import com.dnd12th_4.pickitalki.domain.channel.ChannelRepository;
import com.dnd12th_4.pickitalki.domain.channel.Role;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.member.MemberRepository;
import com.dnd12th_4.pickitalki.domain.question.QuestionRepository;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.INVITEDALL;
import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.MADEALL;
import static com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums.SHOWALL;
import static io.micrometer.common.util.StringUtils.isBlank;

@Transactional
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

        return member.getChannelMembers().stream()
                .filter(channelMember -> status == SHOWALL ||
                        (status == INVITEDALL && channelMember.getRole() == Role.MEMBER) ||
                        (status == MADEALL && channelMember.getRole() == Role.OWNER)
                )
                .map(this::buildChannelShowAllResponse)
                .toList();
    }

    private ChannelShowAllResponse buildChannelShowAllResponse(ChannelMember channelMember) {

        Channel channel = channelMember.getChannel();

        String ownerName = channel.getChannelMembers().stream()
                .filter(it -> it.getRole() == Role.OWNER && it.getChannel().getUuid().equals(channel.getUuid()))
                .findAny()
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 채널의 owner를 찾을 수 없습니다"))
                .getMemberCodeName();

        long signalCount = questionRepository.countByChannelUuid(channel.getUuid());

        return ChannelShowAllResponse.builder()
                .channelId(channel.getId())
                .channelOwnerName(ownerName)
                .channelRoomName(channel.getName())
                .countPerson((long) channel.getChannelMembers().size())
                .signalCount(signalCount)
                .inviteCode(channel.getInviteCode())
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
                .inviteCode(channel.getInviteCode())
                .build();

    }

    @Transactional(readOnly= true)
    public ChannelMemberResponse findChannelMembers(Long memberId, String channelId) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널을 찾을 수 없습니다. 채널의 회원정보를 응답할 수 없습니다."));
        channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("채널에 해당 회원이 존재하지 않습니다. 채널의 회원정보들을 조회할 권한이 없습니다."));

        List<ChannelMemberDto> channelMemberDtos = channel.getChannelMembers()
                .stream().map(channelMember -> ChannelMemberDto.builder()
                        .nickName(channelMember.getMemberCodeName())
                        .profileImageUrl(channelMember.getMember().getProfileImageUrl())
                        .channelMemberId(channelMember.getId())
                        .build()
                ).toList();

        return ChannelMemberResponse.builder()
                .channelMembers(channelMemberDtos)
                .channelName(channel.getName())
                .memberCount(channelMemberDtos.size())
                .build();
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
                .inviteCode(channel.getInviteCode())
                .build();
    }

    public ChannelStatusResponse findChannelStatus(Long memberId, String channelId) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널을 찾을 수 없습니다. 채널의 상태정보를 응답할 수 없습니다."));
        channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("채널에 해당 회원이 존재하지 않습니다. 채널의 상태정보를 조회할 권한이 없습니다."));

        return ChannelStatusResponse.builder()
                .channelName(channel.getName())
                .channelId(channel.getId())
                .level(channel.getLevel())
                .point(channel.getPoint())
                .characterImageUri(AppConfig.getBaseUrl()+ChannelLevel.getImageByLevel(channel.getLevel()))
                .build();
        //TODO 멤버의 조회 시에 오늘 채널 몇개 중에 몇개의 응답을 했는지 정보 반환하는 api필요
    }

    public MyChannelMemberResponse updateChannelMemberProfile(Long memberId, String channelId, String codeName, String imageUrl) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널을 찾을 수 없습니다. 회원정보를 수정할 수 없습니다."));
        ChannelMember channelMember = channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널에 참여해 있지 않습니다. 회원정보를 수정할 권한이 없습니다."));

        if (StringUtils.isNotBlank(codeName)) {
            channelMember.setMemberCodeName(codeName);
        }
        if (StringUtils.isNotBlank(imageUrl)) {
            channelMember.setCustomProfileImage(imageUrl);
        }

        return MyChannelMemberResponse.builder()
                .channelId(channel.getId())
                .channelMemberId(channelMember.getId())
                .channelName(channel.getName())
                .codeName(channelMember.getMemberCodeName())
                .profileImage(channelMember.getProfileImage())
                .build();
    }

    public void leaveChannel(Long memberId, String channelId) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널을 찾을 수 없습니다. 탈퇴할 수 없습니다."));
        ChannelMember channelMember = channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널에 참여해 있지 않습니다. 탈퇴할 수 없습니다."));

        channel.leaveChannel(channelMember);
    }

    public void leaveChannels(Long memberId, List<String> channelIds) {
        for (String channelId : channelIds) {
            leaveChannel(memberId, channelId);
        }
    }

    public void deleteChannel(Long memberId, String channelId) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널을 찾을 수 없습니다. 탈퇴할 수 없습니다."));
        ChannelMember channelMember = channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널에 참여해 있지 않습니다. 탈퇴할 수 없습니다."));

        if (channelMember.getRole() != Role.OWNER) {
            throw new IllegalArgumentException("채널의 개설자가 아닙니다. 채널을 삭제할 권한이 없습니다.");
        }

        channelRepository.deleteById(channelUuid);
    }
}
