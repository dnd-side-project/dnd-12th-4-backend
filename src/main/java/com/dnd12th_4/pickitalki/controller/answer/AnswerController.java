package com.dnd12th_4.pickitalki.controller.answer;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.controller.answer.dto.request.AnswerRequest;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerResponse;
import com.dnd12th_4.pickitalki.domain.answer.AnswerRepository;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.service.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/answer")
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping("/{questionId}")
    public Api<AnswerResponse> writeAnswer(
            @PathVariable("questionId") Long questionId,
            @MemberId Long memberId,
            @Valid @RequestBody AnswerRequest answerRequest) {

        AnswerResponse answerResponse = answerService.save(questionId,memberId,answerRequest);
        return Api.CREATED(answerResponse);
    }
}
//Todo answer를 작성할시 questionId를 받는게 맞나?? channelUUID로 해야되나??
//Todo answerResponse 에서 답변 리스트 에 해당하는 반환값 좀더 자세히 작성하기