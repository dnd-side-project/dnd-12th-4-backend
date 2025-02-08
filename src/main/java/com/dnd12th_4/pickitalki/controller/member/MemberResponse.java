package com.dnd12th_4.pickitalki.controller.member;

import lombok.Builder;

@Builder
public record MemberResponse(String name, String email, String profileImage) {
}
