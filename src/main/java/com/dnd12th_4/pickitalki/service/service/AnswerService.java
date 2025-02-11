package com.dnd12th_4.pickitalki.service.service;

import com.dnd12th_4.pickitalki.controller.answer.dto.request.AnswerRequest;
import com.dnd12th_4.pickitalki.controller.answer.dto.request.AnswerUpdateRequest;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.*;
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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public AnswerWriteResponse save(Long questionId, Long memberId, AnswerRequest requestForm) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApiException(AnswerErrorCode.INVALID_ARGUMENT, "answer save 실패"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(MemberErrorCode.INVALID_ARGUMENT, "AnswerResponse save 에서 member찾기 실패"));

        ChannelMember channelMember = getChannelMember(member, question);

        AnswerWriteResponse answerWriteResponse = toAnswerWriteResponse(question, channelMember, requestForm);

        Answer answer = new Answer(question, channelMember, requestForm.answerForm(), requestForm.isAnonymous(), requestForm.anonymousName());

        answerRepository.save(answer);

        return answerWriteResponse;
    }


    @Transactional
    public AnswerShowAllResponse showAnswers(Long questionId, Long memberId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApiException(AnswerErrorCode.INVALID_ARGUMENT, "answer save 실패"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(MemberErrorCode.INVALID_ARGUMENT, "AnswerResponse save 에서 member찾기 실패"));

        AnswerShowAllResponse answerShowAllResponse = toAnswerInfoResponse(question, memberId);

        return answerShowAllResponse;
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

        AnswerResponse answerResponse = getAnswerResponse(answer, answer.getAuthor().getMember().getId());

        return AnswerUpdateResponse.builder()
                .answerResponse(answerResponse)
                .build();
    }

    @Transactional
    public AnswerShowAllResponse delete(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "AnswerInfoResponse delete 해당 answerId는 DB에 없습니다."));

        answer.softDelete();

        return toAnswerInfoResponse(answer.getQuestion(), answer.getAuthor().getMember().getId());

    }

    private AnswerResponse getAnswerResponse(Answer answer, Long memberId) {

        String codeName = answer.isAnonymous() ?
                answer.getAnonymousName() : answer.getAuthor().getMemberCodeName();

        boolean isMyAnswer = answer.getAuthor().isSameMember(memberId);

        return AnswerResponse.builder()
                .id(answer.getId())
                .codeName(codeName)
                .content(answer.getContent())
                .updatedAt(answer.getUpdatedAt())
                .createdAt(answer.getCreatedAt())
                .isMyAnswer(isMyAnswer)
                .build();
    }
    private ChannelMember getChannelMember(Member member, Question question) {
        return member.getChannelMembers().stream()
                .filter(it -> it.getChannel().getId().equals(question.getChannel().getId()))
                .findAny()
                .orElseThrow(() -> new ApiException(MemberErrorCode.INVALID_ARGUMENT, "AnswerResponse save 에서 MemberEntity 찾기 실패"));
    }

    private AnswerShowAllResponse toAnswerInfoResponse(Question question, Long memberId) {

        AnswerQuestionDTO questionDTO = AnswerQuestionDTO.builder()
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .codeName(question.getAuthor().getMemberCodeName())
                .content(question.getContent())
                .build();


        List<AnswerResponse> answers = question.getAnswerList()
                .stream()
                .filter(it -> it.isDeleted() == false)
                .map(answer -> {

                    return getAnswerResponse(answer, memberId);
                }).toList();

        return AnswerShowAllResponse.builder()
                .signalCount(answers.size())
                .questionDTO(questionDTO)
                .answerList(answers)
                .build();
    }


    public AnswerWriteResponse toAnswerWriteResponse(Question question, ChannelMember channelMember, AnswerRequest request) {
        List<Answer> answerList = question.getAnswerList();

        int nowSignal = answerList.size() + 1;

        String nowContent = request.answerForm();

        String nowAuthor = request.isAnonymous() ?
                request.anonymousName() : channelMember.getMemberCodeName();

        List<AnswerResponse> answers = question.getAnswerList()
                .stream()
                .filter(it -> it.isDeleted() == false)
                .map(answer -> {
                    return getAnswerResponse(answer, channelMember.getMember().getId());
                }).toList();

        return new AnswerWriteResponse(nowSignal, nowAuthor, nowContent, answers);
    }

}
