package com.dnd12th_4.pickitalki.controller.channel.dto.response;

import jakarta.validation.constraints.NotBlank;

public record MemberCodeNameResponse(@NotBlank String codeName) {
}
