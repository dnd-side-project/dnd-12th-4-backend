package com.dnd12th_4.pickitalki.controller.member;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.controller.member.dto.ImageResponse;
import com.dnd12th_4.pickitalki.service.login.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
}

