package com.dnd12th_4.pickitalki.controller.question;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.common.dto.request.PageParamRequest;
import com.dnd12th_4.pickitalki.common.pagination.Pagination;
import com.dnd12th_4.pickitalki.controller.question.dto.*;
import com.dnd12th_4.pickitalki.service.question.QuestionService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/{channelId}/questions")
    public ResponseEntity<QuestionCreateResponse> createQuestion(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId,
            @RequestBody QuestionCreateRequest request
    ) {
        Long questionId = questionService.save(memberId, channelId, request.content(),
                request.isAnonymous(), request.anonymousName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(QuestionCreateResponse.builder()
                        .questionId(questionId)
                        .build()
                );
    }

    @GetMapping("/questions")
    public ResponseEntity<QuestionShowAllResponse> findQuestionsByMember(
            @Parameter(hidden = true) @ModelAttribute PageParamRequest pageParamRequest,
            @MemberId Long memberId,
            @RequestParam(value = "tab", defaultValue = "all") String questionFilter,
            @RequestParam(value = "sort", defaultValue = "latest") String sort
    ) {
        Pageable pageable = Pagination.validateGetPage(sort, pageParamRequest);
        QuestionControllerEnums questionFilterEnum = QuestionControllerEnums.from(questionFilter);

        QuestionShowAllResponse questionShowAllResponse = questionService.findQuestionsByMember(memberId, pageable, questionFilterEnum);

        return ResponseEntity.ok()
                .body(questionShowAllResponse);
    }

    @GetMapping("/{channelId}/questions")
    public ResponseEntity<QuestionShowAllResponse> findQuestionsByChannel(
            @Parameter(hidden = true) @ModelAttribute PageParamRequest pageParamRequest,
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId,
            @RequestParam(value = "tab", defaultValue = "all") String questionFilter,
            @RequestParam(value = "sort", defaultValue = "latest") String sort
    ) {
        Pageable pageable = Pagination.validateGetPage(sort, pageParamRequest);
        QuestionControllerEnums questionFilterEnum = QuestionControllerEnums.from(questionFilter);
        return ResponseEntity.ok().body(questionService.findByChannelId(memberId, channelId, pageable, questionFilterEnum));
    }


    @GetMapping("/{channelId}/questions/{questionId}")
    public ResponseEntity<QuestionOneResponse> findQuestionsByQuestionId(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId,
            @PathVariable("questionId") Long questionId
    ) {

        QuestionOneResponse questionOneResponse = questionService.findQuestionById(memberId, questionId);

        return ResponseEntity.ok()
                .body(questionOneResponse);
    }

    @GetMapping("/{channelId}/questions/today")
    public ResponseEntity<TodayQuestionResponse> findTodayQuestionByChannel(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId
    ) {
        TodayQuestionResponse todayQuestionResponse = questionService.findTodayQuestion(memberId, channelId);

        return ResponseEntity.ok()
                .body(todayQuestionResponse);
    }

    @PutMapping("/{channelId}/questions/{questionId}")
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

    @DeleteMapping("/{channelId}/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId,
            @PathVariable("questionId") Long questionId
    ) {
        questionService.deleteQuestion(memberId, questionId);
        return ResponseEntity.noContent().build();
    }

}
