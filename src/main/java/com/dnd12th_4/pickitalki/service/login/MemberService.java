package com.dnd12th_4.pickitalki.service.login;


import com.dnd12th_4.pickitalki.common.config.AppConfig;
import com.dnd12th_4.pickitalki.common.dto.request.PageParamRequest;
import com.dnd12th_4.pickitalki.common.dto.response.PageParamResponse;
import com.dnd12th_4.pickitalki.controller.channel.ChannelControllerEnums;
import com.dnd12th_4.pickitalki.controller.member.dto.*;
import com.dnd12th_4.pickitalki.domain.answer.Answer;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMemberRepository;
import com.dnd12th_4.pickitalki.domain.channel.Role;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.member.MemberRepository;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import static io.micrometer.common.util.StringUtils.isNotBlank;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private static final String PROFILE_IMAGE_DIRECTORY = "/app/images/profile/";

    private final MemberRepository memberRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final AppConfig appConfig;

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
    public MyChannelMemberShowAllResponse findAllChannelMyInfo(Long memberId, ChannelControllerEnums status, Pageable pageable) {

        Page<ChannelMember> pageChannelMember = channelMemberRepository.findByMemberId(memberId, pageable);


        List<MyChannelMemberResponse> filteredList = pageChannelMember.getContent().stream()
                .filter(channelMember ->
                        status == ChannelControllerEnums.SHOWALL ||
                                (status == ChannelControllerEnums.MADEALL && channelMember.getRole() == Role.OWNER) ||
                                (status == ChannelControllerEnums.MADEALL && channelMember.getRole() == Role.MEMBER)
                )
                .map(MemberService::buildChannelMemberResponse)
                .toList();

        Page<MyChannelMemberResponse> page = getPage(pageable, filteredList);

        return MyChannelMemberShowAllResponse.builder()
                .myChannelMemberResponse(page.getContent())
                .pageParamResponse(createPageParamResponse(page))
                .build();
    }


    private static MyChannelMemberResponse buildChannelMemberResponse(ChannelMember channelMember) {
        return MyChannelMemberResponse.builder()
                .channelMemberId(channelMember.getId())
                .channelName(channelMember.getChannel().getName())
                .codeName(channelMember.getMemberCodeName())
                .profileImage(channelMember.getProfileImage())
                .channelId(channelMember.getChannel().getId())
                .countPerson(channelMember.getChannel().getChannelMembers().size())
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
    public ChannelFriendShowAllResponse findChannelFriends(Long memberId, Pageable pageable) {

        List<ChannelMember> meOnChannel = channelMemberRepository.findMeOnChannel(memberId);

        List<ChannelFriendResponse> filteredList = meOnChannel.stream()
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

        Page<ChannelFriendResponse> page = getPage(pageable, filteredList);

        return ChannelFriendShowAllResponse.builder()
                .channelFriendResponseList(page.getContent())
                .pageParamResponse(createPageParamResponse(page))
                .build();
    }

    public ImageResponse uploadProfileImage(Long memberId, MultipartFile file) {
        try {
            String fileName = uploadImage(memberId, file);

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 존재하지 않습니다. 이미지를 업로드할 수 없습니다."));

            return ImageResponse.builder()
                    .imageUrl(appConfig.getBaseUrl() + "/images/profile/" + fileName)
                    .memberId(member.getId())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드에 실패했습니다." + e.getMessage());
        }
    }

    private String uploadImage(Long memberId, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();

        if (StringUtils.isNotBlank(originalFilename) && !originalFilename.matches(".*\\.(jpg|jpeg|png|gif|svg)$")) {
            throw new IllegalArgumentException("지원되지 않는 파일 형식입니다.");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        String fileName = memberId + "_" + UUID.randomUUID() + extension;
        Path filePath = Paths.get(PROFILE_IMAGE_DIRECTORY + fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }

    public MemberResponse updateMemberProfile(Long memberId, String nickName, String imageUrl) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 존재하지 않습니다. 회원정보를 수정할 수 없습니다."));

        if (isNotBlank(nickName)) {
            member.setNickName(nickName);
        }
        if (isNotBlank(imageUrl)) {
            member.setProfileImageUrl(imageUrl);
        }

        return MemberResponse.builder()
                .name(member.getNickName())
                .email(member.getEmail())
                .profileImage(member.getProfileImageUrl())
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
}
