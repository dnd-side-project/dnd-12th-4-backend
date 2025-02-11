package com.dnd12th_4.pickitalki.controller.question;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.controller.question.dto.QuestionCreateRequest;
import com.dnd12th_4.pickitalki.controller.question.dto.QuestionCreateResponse;
import com.dnd12th_4.pickitalki.controller.question.dto.QuestionResponse;
import com.dnd12th_4.pickitalki.controller.question.dto.QuestionUpdateRequest;
import com.dnd12th_4.pickitalki.controller.question.dto.QuestionUpdateResponse;
import com.dnd12th_4.pickitalki.controller.question.dto.TodayQuestionResponse;
import com.dnd12th_4.pickitalki.service.question.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<QuestionResponse>> findQuestionsByChannel(
            @MemberId Long memberId,
            @PathVariable("channelId") String channelId
    ) {
        List<QuestionResponse> questionResponses = questionService.findByChannelId(memberId, channelId);

        return ResponseEntity.ok()
                .body(questionResponses);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionUpdateResponse> updateQuestion(
            @MemberId Long memberId,
            @RequestBody QuestionUpdateRequest request
    ) {
        QuestionUpdateResponse questionUpdateResponse = questionService.updateQuestion(memberId, request.questionId(), request.content());

        return ResponseEntity.ok()
                .body(questionUpdateResponse);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @MemberId Long memberId,
            @PathVariable("questionId") Long questionId
    ) {
        questionService.deleteQuestion(memberId, questionId);
        return ResponseEntity.noContent().build();
    }

}
