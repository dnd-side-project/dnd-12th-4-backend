package com.dnd12th_4.pickitalki.controller.answer;

import com.dnd12th_4.pickitalki.common.annotation.MemberId;
import com.dnd12th_4.pickitalki.common.dto.request.PageParamRequest;
import com.dnd12th_4.pickitalki.controller.answer.dto.request.AnswerRequest;
import com.dnd12th_4.pickitalki.controller.answer.dto.request.AnswerUpdateRequest;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerShowAllResponse;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerWriteResponse;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerUpdateResponse;
import com.dnd12th_4.pickitalki.presentation.api.Api;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import com.dnd12th_4.pickitalki.service.answer.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/answer")
public class AnswerController {

    private final AnswerService answerService;

    @GetMapping("/{questionId}")
    public Api<AnswerShowAllResponse> showAnswers(
            @RequestParam(value = "filter", defaultValue = "DESC") String filter,
            @ModelAttribute @Valid PageParamRequest pageParamRequest,
            @PathVariable("questionId") Long questionId,
            @MemberId Long memberId
    ){
        Pageable pageable = validateGetPage(filter, pageParamRequest);

        AnswerShowAllResponse answerShowAllResponse = answerService.showAnswers(questionId,memberId, pageable);
        return Api.OK(answerShowAllResponse);
    }


    @PostMapping("/{questionId}")
    public Api<AnswerWriteResponse> writeAnswer(
            @PathVariable("questionId") Long questionId,
            @MemberId Long memberId,
            @Valid @RequestBody AnswerRequest answerRequest) {


        AnswerWriteResponse answerWriteResponse = answerService.save(questionId,memberId,answerRequest);
        return Api.CREATED(answerWriteResponse);
    }

    @PutMapping("/{answerId}")
    public Api<AnswerUpdateResponse> updateAnswer(

            @PathVariable("answerId") Long answerId,
            @Valid @RequestBody AnswerUpdateRequest answerUpdateRequest
    ){
        AnswerUpdateResponse answerUpdateResponse =answerService.update(answerId, answerUpdateRequest);
        return Api.OK(answerUpdateResponse);

    }


    @DeleteMapping("/{answerId}")
    public Api<String> deleteAnswer(
            @PathVariable("answerId") Long answerId
    ){
        Long deletedId = answerService.delete(answerId);
        return Api.OK("삭제완료 answerId : "+deletedId);
    }

    private  Pageable validateGetPage(String filter, PageParamRequest pageParamRequest) {

        Pageable pageable = null;
        if(filter.equals("DESC")){
            pageable = PageRequest.of(pageParamRequest.getPage(), pageParamRequest.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        else if(filter.equals("ASC")){
            pageable = PageRequest.of(pageParamRequest.getPage(), pageParamRequest.getSize(), Sort.by(Sort.Direction.ASC, "createdAt"));
        }
        else{
            throw new ApiException(ErrorCode.BAD_REQUEST,"filter값을 확인해주세요");
        }
        return pageable;
    }

}
