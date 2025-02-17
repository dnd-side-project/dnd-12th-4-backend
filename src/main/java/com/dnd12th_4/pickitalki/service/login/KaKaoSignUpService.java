package com.dnd12th_4.pickitalki.service.login;

import com.dnd12th_4.pickitalki.common.token.SHA256Util;
import com.dnd12th_4.pickitalki.controller.login.dto.response.KakaoUserDto;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.member.MemberRepository;
import com.dnd12th_4.pickitalki.presentation.error.MemberErrorCode;
import com.dnd12th_4.pickitalki.presentation.error.TokenErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KaKaoSignUpService {

    private final MemberRepository memberRepository;

    @Transactional
    public Member registerOrLoginKakaoUser(KakaoUserDto kakaoUserDto) {
        Optional<Member> existingUser = memberRepository.findByKakaoId(Long.valueOf(kakaoUserDto.getId()));

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        Member newUser = Member.builder()
                .kakaoId(Long.parseLong(kakaoUserDto.getId()))
                .email(kakaoUserDto.getEmail())
                .nickName(kakaoUserDto.getNickname())
                .profileImageUrl(kakaoUserDto.getProfileImageUrl())
                .build();

        return memberRepository.save(newUser);
    }

    public Member saveUserEntity(Member member) {
        return Optional.ofNullable(member)
                .map(memberRepository::save)
                .orElseThrow(() -> new ApiException(MemberErrorCode.INVALID_ARGUMENT, "Member saveUserEntity 42번째줄 에러"));

    }

    public Member findUser(String token) {

        String hashedRefreshToken = SHA256Util.hash(token);

        return memberRepository.findByRefreshToken(hashedRefreshToken)
                .orElseThrow(() -> new ApiException(TokenErrorCode.INVALID_TOKEN, "refreshToken 을 가진 사용자는 없습니다. 위치 : findUser"));
    }
}
