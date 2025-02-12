package com.dnd12th_4.pickitalki.service.login;


import com.dnd12th_4.pickitalki.controller.channel.dto.ChannelControllerEnums;
import com.dnd12th_4.pickitalki.controller.member.dto.ChannelFriendResponse;
import com.dnd12th_4.pickitalki.controller.member.dto.ImageResponse;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private static final String IMAGE_DIRECTORY = "/app/images/profiles/";
    private static final String BASE_URL = "https://your-server.com/images/profiles/";

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

    public ImageResponse uploadProfileImage(Long memberId, MultipartFile file) {
        try {
            String fileName = uploadImage(memberId, file);

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new EmptyResultDataAccessException("해당하는 회원이 존재하지 않습니다. 이미지를 업로드할 수 없습니다.", 1));

            return ImageResponse.builder()
                    .imageUrl(BASE_URL + fileName)
                    .memberId(member.getId())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드에 실패했습니다." + e.getMessage());
        }
    }

    private String uploadImage(Long memberId, MultipartFile file) throws IOException {
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일이 아닙니다. 업로드할 수 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";

        // 확장자가 있는지 확인 후 추출
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = memberId + "_" + UUID.randomUUID() + extension;
        Path filePath = Paths.get(IMAGE_DIRECTORY + fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return fileName;
    }
}
