package com.dnd12th_4.pickitalki.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByKakaoId(Long kakaoId);

    Optional<MemberEntity> findByRefreshToken(String refreshToken);
}
