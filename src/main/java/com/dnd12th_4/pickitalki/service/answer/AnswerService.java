package com.dnd12th_4.pickitalki.service.answer;

import com.dnd12th_4.pickitalki.common.converter.DateTimeUtil;
import com.dnd12th_4.pickitalki.common.dto.response.PageParamResponse;
import com.dnd12th_4.pickitalki.controller.answer.dto.request.AnswerRequest;
import com.dnd12th_4.pickitalki.controller.answer.dto.request.AnswerUpdateRequest;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerQuestionDTO;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerResponse;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerShowAllResponse;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerUpdateResponse;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerWriteResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

        validateTodayRespond(memberId, question);

        Answer answer = new Answer(question, channelMember, requestForm.answerForm(), requestForm.isAnonymous(), requestForm.anonymousName());
        answerRepository.save(answer);

        return toAnswerWriteResponse(answer, member);
    }

    private void validateTodayRespond(Long memberId, Question question) {
        boolean alreadyAnswered = question.getAnswerList().stream()
                .anyMatch(answer -> answer.getAuthor().isSameMember(memberId));

        if (alreadyAnswered) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "이 사용자는 이미 오늘의 질문에 답을 했습니다.");
        }
    }

    @Transactional
    public AnswerShowAllResponse showAnswers(Long questionId, Long memberId,Pageable pageable) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ApiException(AnswerErrorCode.INVALID_ARGUMENT, "answer save 실패"));
        memberRepository.findById(memberId)
                .orElseThrow(() -> new ApiException(MemberErrorCode.INVALID_ARGUMENT, "AnswerResponse save 에서 member찾기 실패"));

        Page<Answer> answerPage = answerRepository.findByQuestionIdAndIsDeletedFalse(questionId,pageable);

        return toAnswerInfoResponse(question, memberId, answerPage);
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

        AnswerResponse answerResponse = toAnswerResponse(answer, answer.getAuthor().getMember().getId());

        return AnswerUpdateResponse.builder()
                .answerResponse(answerResponse)
                .build();
    }

    @Transactional
    public void delete(Long answerId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ApiException(ErrorCode.BAD_REQUEST, "AnswerInfoResponse delete 해당 answerId는 DB에 없습니다."));

        Question question = answer.getQuestion();
        question.getAnswerList().remove(answer);
    }

    private AnswerResponse toAnswerResponse(Answer answer, Long memberId) {

        String codeName = answer.isAnonymous() ?
                answer.getAnonymousName() : answer.getAuthor().getMemberCodeName();

        boolean isMyAnswer = answer.getAuthor().isSameMember(memberId);

        return AnswerResponse.builder()
                .id(answer.getId())
                .codeName(codeName)
                .writerProfileImage(answer.getAuthor().getProfileImage())
                .content(answer.getContent())
                .updatedAt(DateTimeUtil.toKstString(answer.getUpdatedAt()))
                .createdAt(DateTimeUtil.toKstString(answer.getCreatedAt()))
                .isMyAnswer(isMyAnswer)
                .build();
    }
    private ChannelMember getChannelMember(Member member, Question question) {
        return member.getChannelMembers().stream()
                .filter(it -> it.getChannel().getId().equals(question.getChannel().getId()))
                .findAny()
                .orElseThrow(() -> new ApiException(MemberErrorCode.INVALID_ARGUMENT, "AnswerResponse save 에서 MemberEntity 찾기 실패"));
    }

    private AnswerShowAllResponse toAnswerInfoResponse(Question question, Long memberId, Page<Answer> answerPage) {

        AnswerQuestionDTO questionDTO = AnswerQuestionDTO.builder()
                .createdAt(DateTimeUtil.toKstString(question.getCreatedAt()))
                .updatedAt(DateTimeUtil.toKstString(question.getUpdatedAt()))
                .codeName(question.getWriterName())
                .writerProfileImage(question.getWriter().getProfileImage())
                .content(question.getContent())
                .build();

        PageParamResponse pageParamResponse = PageParamResponse.builder()
                .currentPage(answerPage.getNumber())
                .size(answerPage.getSize())
                .totalElements((int) answerPage.getTotalElements())
                .totalPages(answerPage.getTotalPages())
                .hasNext(answerPage.hasNext())
                .build();


        List<AnswerResponse> answers = answerPage.getContent()
                .stream()
                .map(answer -> toAnswerResponse(answer, memberId))
                .toList();

        return AnswerShowAllResponse.builder()
                .signalCount(answers.size())
                .questionDTO(questionDTO)
                .answerList(answers)
                .pageParamResponse(pageParamResponse)
                .build();
    }

    private AnswerWriteResponse toAnswerWriteResponse(Answer answer, Member member) {
        AnswerResponse answerResponse = toAnswerResponse(answer, member.getId());

        AnswerWriteResponse answerWriteResponse = AnswerWriteResponse.builder()
                .answerResponse(answerResponse)
                .build();
        return answerWriteResponse;
    }

}
