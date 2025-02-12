package com.dnd12th_4.pickitalki.controller.member;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.controller.member.dto.ImageResponse;
import com.dnd12th_4.pickitalki.controller.member.dto.MemberResponse;
import com.dnd12th_4.pickitalki.controller.member.dto.MemberUpdateRequest;
import com.dnd12th_4.pickitalki.service.login.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/profile-image")
public class ProfileImageController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ImageResponse> uploadProfileImage(
            @MemberId Long memberId,
            @RequestParam("file") MultipartFile file
    ) {
        ImageResponse imageResponse = memberService.uploadProfileImage(memberId, file);

        return ResponseEntity.status(CREATED)
                .body(imageResponse);
    }

    @PatchMapping
    public ResponseEntity<MemberResponse> updateMemberProfile(
            @MemberId Long memberId,
            @RequestBody MemberUpdateRequest request
    ) {
        MemberResponse memberResponse = memberService.updateMemberProfile(memberId, request.nickName(), request.image());

        return ResponseEntity.status(CREATED)
                .body(memberResponse);
    }

}

