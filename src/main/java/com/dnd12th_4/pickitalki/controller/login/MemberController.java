package com.dnd12th_4.pickitalki.controller.login;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.controller.login.dto.response.TutorialResponse;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.member.Tutorial;
import com.dnd12th_4.pickitalki.domain.member.TutorialStatus;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.service.login.MemberService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
