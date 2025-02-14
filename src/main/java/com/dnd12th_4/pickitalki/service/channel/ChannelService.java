package com.dnd12th_4.pickitalki.service.channel;

import com.dnd12th_4.pickitalki.common.config.AppConfig;
import com.dnd12th_4.pickitalki.common.dto.response.PageParamResponse;
import com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums;
import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelMemberDto;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelJoinResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelMemberProfileResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelMembersResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelShowAllResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelShowResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelSpecificResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelStatusResponse;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.MemberCodeNameResponse;
import com.dnd12th_4.pickitalki.controller.member.dto.MyChannelMemberResponse;
import com.dnd12th_4.pickitalki.domain.channel.Channel;
import com.dnd12th_4.pickitalki.domain.channel.ChannelLevel;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMemberRepository;
import com.dnd12th_4.pickitalki.domain.channel.ChannelRepository;
import com.dnd12th_4.pickitalki.domain.channel.Role;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.member.MemberRepository;
import com.dnd12th_4.pickitalki.domain.question.Question;
import com.dnd12th_4.pickitalki.domain.question.QuestionRepository;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
    public ChannelShowAllResponse findAllMyChannels(Long memberId, ChannelControllerEnums status, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "존재하지 않는 회원입니다. 참여한 채널들을 조회할 수 없습니다."));

        List<ChannelShowResponse> filteredList = member.getChannelMembers().stream()
                .filter(channelMember -> status == SHOWALL ||
                        (status == INVITEDALL && channelMember.getRole() == Role.MEMBER) ||
                        (status == MADEALL && channelMember.getRole() == Role.OWNER)
                )
                .map(this::buildChannelShowAllResponse)
                .toList();

        Page<ChannelShowResponse> page = getPage(pageable, filteredList);

        return ChannelShowAllResponse.builder()
                .channelShowResponse(page.getContent())
                .pageParamResponse(createPageParamResponse(page))
                .build();
    }

    private ChannelShowResponse buildChannelShowAllResponse(ChannelMember channelMember) {

        Channel channel = channelMember.getChannel();

        String ownerName = channel.getChannelMembers().stream()
                .filter(it -> it.getRole() == Role.OWNER && it.getChannel().getUuid().equals(channel.getUuid()))
                .findAny()
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 채널의 owner를 찾을 수 없습니다"))
                .getMemberCodeName();

        long signalCount = questionRepository.countByChannelUuid(channel.getUuid());

        return ChannelShowResponse.builder()
                .channelId(channel.getId())
                .channelOwnerName(ownerName)
                .channelRoomName(channel.getName())
                .countPerson((long) channel.getChannelMembers().size())
                .signalCount(signalCount)
                .inviteCode(channel.getInviteCode())
                .createdAt(channel.getCreatedAt().toString())
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
    public ChannelMembersResponse findChannelMembers(Long memberId, String channelId, Pageable pageable) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널을 찾을 수 없습니다. 채널의 회원정보를 응답할 수 없습니다."));
        channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("채널에 해당 회원이 존재하지 않습니다. 채널의 회원정보들을 조회할 권한이 없습니다."));


        List<ChannelMemberDto> filteredList = channel.getChannelMembers()
                .stream().map(channelMember -> ChannelMemberDto.builder()
                        .codeName(channelMember.getMemberCodeName())
                        .profileImageUrl(channelMember.getMember().getProfileImageUrl())
                        .channelMemberId(channelMember.getId())
                        .build()
                ).toList();

        Page<ChannelMemberDto> page = getPage(pageable, filteredList);

        return new ChannelMembersResponse(channel.getName(),page.getContent().size(), page.getContent(), createPageParamResponse(page));
    }

    public ChannelMemberProfileResponse findChannelMember(Long memberId, String channelId) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널을 찾을 수 없습니다. 채널의 회원정보를 응답할 수 없습니다."));
        ChannelMember channelMember = channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("채널에 해당 회원이 존재하지 않습니다. 채널의 회원정보들을 조회할 권한이 없습니다."));

        ChannelMember todayQuestioner = findTodayQuestioner(channel);

        return ChannelMemberProfileResponse.builder()
                .channelMemberId(channelMember.getId())
                .codeName(channelMember.getMemberCodeName())
                .profileImageUrl(channelMember.getMember().getProfileImageUrl())
                .isTodayQuestioner(channelMember.getId().equals(todayQuestioner.getId()))
                .build();
    }

    public ChannelMemberProfileResponse findTodayQuestioner(Long memberId, String channelId) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널을 찾을 수 없습니다. 오늘의 질문자를 응답할 수 없습니다."));
        channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("채널에 해당 회원이 존재하지 않습니다. 오늘의 질문자를 조회할 권한이 없습니다."));

        ChannelMember todayQuestioner = findTodayQuestioner(channel);

        return ChannelMemberProfileResponse.builder()
                .channelMemberId(todayQuestioner.getId())
                .codeName(todayQuestioner.getMemberCodeName())
                .profileImageUrl(todayQuestioner.getProfileImage())
                .isTodayQuestioner(true)
                .build();
    }

    private ChannelMember findTodayQuestioner(Channel channel) {
        ChannelMember todayQuestioner;
        Optional<Question> todayQuestion = questionRepository.findTodayQuestion(channel.getUuid());

        if (todayQuestion.isPresent()) {
            todayQuestioner = todayQuestion.get().getWriter();
        } else {
            todayQuestioner = channel.pickTodayQuestioner();
        }

        return todayQuestioner;
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
                .countPerson(channel.getChannelMembers().size())
                .codeName(channelMember.getMemberCodeName())
                .profileImage(channelMember.getProfileImage())
                .build();
    }

    private <T> Page<T> getPage(Pageable pageable, List<T> list) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    private PageParamResponse createPageParamResponse(Page<?> page) {
        return new PageParamResponse(
                page.getNumber(),
                page.getSize(),
                (int) page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
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
