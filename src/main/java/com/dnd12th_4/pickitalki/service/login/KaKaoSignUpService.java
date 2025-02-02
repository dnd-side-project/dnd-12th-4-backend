package com.dnd12th_4.pickitalki.service.login;

import com.dnd12th_4.pickitalki.controller.login.dto.KakaoUserDto;
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

        if(existingUser.isPresent()){
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

    public Member saveUserEntity(Member member){
        return Optional.ofNullable(member)
                .map(memberRepository::save)
                .orElseThrow(()-> new ApiException(MemberErrorCode.INVALID_ARGUMENT,"Member saveUserEntity 42번째줄 에러"));

    }

    public Member findUser(String token){
        return memberRepository.findByRefreshToken(token)
                .orElseThrow(() -> new ApiException(TokenErrorCode.INVALID_TOKEN,"Member findUser 48번째줄 에러"));
    }
}
