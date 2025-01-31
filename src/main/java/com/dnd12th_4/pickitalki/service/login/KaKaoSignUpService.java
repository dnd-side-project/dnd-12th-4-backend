package com.dnd12th_4.pickitalki.service.login;

import com.dnd12th_4.pickitalki.controller.login.dto.KakaoUserDto;
import com.dnd12th_4.pickitalki.domain.member.MemberEntity;
import com.dnd12th_4.pickitalki.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KaKaoSignUpService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberEntity registerOrLoginKakaoUser(KakaoUserDto kakaoUserDto) {
        Optional<MemberEntity> existingUser = memberRepository.findByKakaoId(Long.valueOf(kakaoUserDto.getId()));

        if(existingUser.isPresent()){
            return existingUser.get();
        }

        MemberEntity newUser = MemberEntity.builder()
                .kakaoId(Long.parseLong(kakaoUserDto.getId()))
                .email(kakaoUserDto.getEmail())
                .nickName(kakaoUserDto.getNickname())
                .profileImageUrl(kakaoUserDto.getProfileImageUrl())
                .build();

        return memberRepository.save(newUser);
    }

    public MemberEntity saveUserEntity(MemberEntity memberEntity){
        return Optional.ofNullable(memberEntity)
                .map(memberRepository::save)
                .orElseThrow(()-> new RuntimeException("유저 엔티티가 null값"));

    }

    public MemberEntity findUser(String token){
        return memberRepository.findByRefreshToken(token)
                .orElseThrow(() -> new RuntimeException("refreshToken error"));
    }
}
