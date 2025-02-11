package com.dnd12th_4.pickitalki.service.login;


import com.dnd12th_4.pickitalki.domain.member.*;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.error.MemberErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TutorialRepository tutorialRepository;

    @Transactional
    public Member updateName(Long memberId, String name) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "해당 member가 DB에 없습니다."));

        member.setNickName(name);

        Tutorial tutorial = buildTutorial(member);
        tutorialRepository.save(tutorial);

        return member;
    }

    private Tutorial buildTutorial(Member member) {
        return Tutorial.builder()
                .memberId(member.getId())
                .status(TutorialStatus.NON_PASS)
                .build();
    }

    public TutorialStatus hasCompletedTutorial(Long memberId) {

        return tutorialRepository.findStatusByMemberId(memberId)
                .orElseThrow(() -> new ApiException(MemberErrorCode.INVALID_ARGUMENT, "Toturial에 찾고자 하는 데이터 없음"));
    }

    @Transactional
    public TutorialStatus update(Long memberId) {
        Tutorial tutorial = tutorialRepository.findByMemberId(memberId)
                .orElseThrow(() -> new ApiException(MemberErrorCode.INVALID_ARGUMENT, "Toturial에 찾고자 하는 데이터 없음"));

        tutorial.setStatus(TutorialStatus.PASS);

        return tutorial.getStatus();
    }
}
