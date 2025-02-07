package com.dnd12th_4.pickitalki.service.service;

import com.dnd12th_4.pickitalki.controller.answer.dto.request.AnswerRequest;
import com.dnd12th_4.pickitalki.controller.answer.dto.request.AnswerUpdateRequest;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerInfoResponse;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerQuestionDTO;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerResponse;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerUpdateResponse;
import com.dnd12th_4.pickitalki.domain.answer.Answer;
import com.dnd12th_4.pickitalki.domain.answer.AnswerRepository;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.member.MemberRepository;
import com.dnd12th_4.pickitalki.domain.question.Question;
import com.dnd12th_4.pickitalki.domain.question.QuestionRepository;
import com.dnd12th_4.pickitalki.presentation.error.AnswerErrorCode;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.error.MemberErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public AnswerResponse save(Long questionId, Long memberId, AnswerRequest requestForm) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApiException(AnswerErrorCode.INVALID_ARGUMENT, "answer save 실패"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(MemberErrorCode.INVALID_ARGUMENT, "AnswerResponse save 에서 member찾기 실패"));

        ChannelMember channelMember = getChannelMember(member, question);

        AnswerResponse answerResponse = toAnswerResponse(question, member, requestForm);

        Answer answer = new Answer(question, channelMember, requestForm.answerForm(), requestForm.isAnonymous(), requestForm.anonymousName());

        answerRepository.save(answer);

        return answerResponse;
    }

    private ChannelMember getChannelMember(Member member, Question question) {
        return member.getChannelMembers().stream()
                .filter(it -> it.getChannel().getId().equals(question.getChannel().getId()))
                .findAny()
                .orElseThrow(() -> new ApiException(MemberErrorCode.INVALID_ARGUMENT, "AnswerResponse save 에서 MemberEntity 찾기 실패"));
    }


    @Transactional
    public AnswerInfoResponse showAnswers(Long questionId, Long memberId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApiException(AnswerErrorCode.INVALID_ARGUMENT, "answer save 실패"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(MemberErrorCode.INVALID_ARGUMENT, "AnswerResponse save 에서 member찾기 실패"));

        AnswerInfoResponse answerInfoResponse = toAnswerInfoResponse(question);

        return answerInfoResponse;
    }

    private AnswerInfoResponse toAnswerInfoResponse(Question question) {

        AnswerQuestionDTO questionDTO = AnswerQuestionDTO.builder()
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .codeName(question.getAuthor().getMemberCodeName())
                .content(question.getContent())
                .build();

        List<Answer> answerList = question.getAnswerList()
                .stream()
                .filter(it-> it.isDeleted()==false)
                .toList();

        return AnswerInfoResponse.builder()
                .signalCount(answerList.size())
                .questionDTO(questionDTO)
                .answerList(answerList)
                .build();
    }


    public AnswerResponse toAnswerResponse(Question question, Member member, AnswerRequest request) {
        List<Answer> answerList = question.getAnswerList();
        int nowSignal = answerList.size() + 1;
        String nowContent = request.answerForm();

        String nowAuthor = request.isAnonymous() ? request.anonymousName() :
                question.getChannel().getChannelMembers().stream()
                        .filter(it -> it.getMember().getId().equals(member.getId()))
                        .findAny()
                        .map(ChannelMember::getMemberCodeName)
                        .orElseThrow(() -> new ApiException(MemberErrorCode.INVALID_ARGUMENT,
                                "AnswerResponse toAnswerResponse에서 해당 member를 찾지 못했습니다."));

        return new AnswerResponse(nowSignal, nowAuthor, nowContent, answerList);
    }

    @Transactional
    public AnswerUpdateResponse update(Long answerId, AnswerUpdateRequest answerUpdateRequest) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "AnswerUpdateResponse update 해당 answerId는 DB에 없습니다."));

        Optional.ofNullable(answerUpdateRequest)
                .ifPresentOrElse(
                        request -> answer.setContent(request.getContent()), // 값이 있으면 실행
                        () -> {
                            throw new ApiException(ErrorCode.BAD_REQUEST, "AnswerUpdateRequest is NULL");
                        }
                );

        return AnswerUpdateResponse.builder()
                .answer(answer)
                .build();
    }

    public AnswerInfoResponse delete(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "AnswerInfoResponse delete 해당 answerId는 DB에 없습니다."));

        answer.softDelete();

        return toAnswerInfoResponse(answer.getQuestion());

    }
}
