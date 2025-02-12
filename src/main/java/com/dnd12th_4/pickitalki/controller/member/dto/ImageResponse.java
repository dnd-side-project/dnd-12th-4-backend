package com.dnd12th_4.pickitalki.controller.member.dto;

import lombok.Builder;

@Builder
public record ImageResponse(String imageUrl, long memberId) {
}
