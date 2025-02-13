package com.dnd12th_4.pickitalki.controller.question;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.common.dto.request.PageParamRequest;
import com.dnd12th_4.pickitalki.controller.question.dto.*;

import com.dnd12th_4.pickitalki.service.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels/{channelId}/questions")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionCreateResponse> createQuestion(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId,
            @RequestBody QuestionCreateRequest request
    ) {
        Long questionId = questionService.save(memberId, channelId, request.content(),
                request.isAnonymous(),request.anonymousName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(QuestionCreateResponse.builder()
                        .questionId(questionId)
                        .build()
                );
    }

    @GetMapping
    public ResponseEntity<QuestionResponse> findTodayQuestionByChannel(
            @PathVariable("channelId") String channelId,
            @MemberId Long memberId,
            @RequestParam("questionId") long questionId
    ) {
        QuestionResponse questionResponse = questionService.findQuestionById(memberId, questionId);

        return ResponseEntity.ok()
                .body(questionResponse);
    }

    @GetMapping("/today")
    public ResponseEntity<TodayQuestionResponse> findTodayQuestionByChannel(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId
    ) {
        TodayQuestionResponse todayQuestionResponse = questionService.findTodayQuestion(memberId, channelId);

        return ResponseEntity.ok()
                .body(todayQuestionResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<QuestionShowAllResponse> findQuestionsByChannel(
            @ModelAttribute PageParamRequest pageParamRequest,
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId
    ) {
        QuestionShowAllResponse questionShowAllResponse = questionService.findByChannelId(memberId, channelId, pageParamRequest);

        return ResponseEntity.ok()
                .body(questionShowAllResponse);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionUpdateResponse> updateQuestion(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId,
            @PathVariable("questionId") Long questionId,
            @RequestBody QuestionUpdateRequest request
    ) {
        QuestionUpdateResponse questionUpdateResponse = questionService.updateQuestion(memberId, questionId, request.content());

        return ResponseEntity.ok()
                .body(questionUpdateResponse);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId,
            @PathVariable("questionId") Long questionId
    ) {
        questionService.deleteQuestion(memberId, questionId);
        return ResponseEntity.noContent().build();
    }

}
