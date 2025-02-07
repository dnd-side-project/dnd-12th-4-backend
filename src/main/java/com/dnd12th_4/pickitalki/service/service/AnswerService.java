package com.dnd12th_4.pickitalki.service.service;

import com.dnd12th_4.pickitalki.controller.answer.dto.request.AnswerRequest;
import com.dnd12th_4.pickitalki.controller.answer.dto.response.AnswerResponse;
import com.dnd12th_4.pickitalki.domain.answer.Answer;
import com.dnd12th_4.pickitalki.domain.answer.AnswerRepository;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.member.MemberRepository;
import com.dnd12th_4.pickitalki.domain.question.Question;
import com.dnd12th_4.pickitalki.domain.question.QuestionRepository;
import com.dnd12th_4.pickitalki.presentation.error.AnswerErrorCode;
import com.dnd12th_4.pickitalki.presentation.error.MemberErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

        AnswerResponse answerResponse = toAnswerResponse(question, member, requestForm);

        Answer answer = new Answer(question, member, requestForm.answerForm(), requestForm.isAnonymous(), requestForm.anonymousName());

        answerRepository.save(answer);


        return answerResponse;
    }

    public AnswerResponse toAnswerResponse(Question question, Member member, AnswerRequest request) {
        List<Answer> answerList = question.getAnswerList();
        int nowSignal = answerList.size()+1;
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
}
