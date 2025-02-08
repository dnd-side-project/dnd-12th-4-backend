package com.dnd12th_4.pickitalki.controller.member;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.controller.login.dto.response.TutorialResponse;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.member.TutorialStatus;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.service.login.MemberService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/name")
    public Api<String> registerName(
            @MemberId Long memberId,
            @RequestParam("name") @NotBlank String name
    ) {
        Member member = memberService.updateName(memberId, name);

        return Api.OK(member.getNickName());
    }

    @GetMapping
    public Api<MemberResponse> findMemberInfo(
            @MemberId Long memberId
    ) {
        MemberResponse memberResponse = memberService.findMemberById(memberId);

        return Api.OK(memberResponse);
    }

    @GetMapping("/channelMembers")
    public Api<List<MyChannelMemberResponse>> findMyChannelMemberInfo(
            @MemberId Long memberId
    ) {
        List<MyChannelMemberResponse> allParticipateMyInfo = memberService.findAllParticipateMyInfo(memberId);

        return Api.OK(allParticipateMyInfo);
    }


    @GetMapping("/tutorial")
    public ResponseEntity<TutorialResponse> doTutorial(
            @MemberId Long memberId
    ){

       TutorialStatus tutorialStatus = memberService.hasCompletedTutorial(memberId);
        TutorialResponse tutorialResponse = new TutorialResponse(tutorialStatus);

        return ResponseEntity.ok(tutorialResponse);
    }

    @PatchMapping("/tutorial/update")
    public ResponseEntity<TutorialResponse> updateTutorial(
            @MemberId Long memberId
    ){

        TutorialStatus tutorialStatus = memberService.update(memberId);
        TutorialResponse tutorialResponse = new TutorialResponse(tutorialStatus);

        return ResponseEntity.ok(tutorialResponse);
    }
}
