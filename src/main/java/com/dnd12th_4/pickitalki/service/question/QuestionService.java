package com.dnd12th_4.pickitalki.service.question;

import com.dnd12th_4.pickitalki.common.dto.response.PageParamResponse;
import com.dnd12th_4.pickitalki.common.pagination.Pagination;
import com.dnd12th_4.pickitalki.controller.channel.dto.response.ChannelMemberProfileResponse;
import com.dnd12th_4.pickitalki.controller.question.QuestionControllerEnums;
import com.dnd12th_4.pickitalki.controller.question.dto.*;
import com.dnd12th_4.pickitalki.domain.channel.Channel;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
import com.dnd12th_4.pickitalki.domain.channel.ChannelRepository;
import com.dnd12th_4.pickitalki.domain.question.Question;
import com.dnd12th_4.pickitalki.domain.question.QuestionRepository;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import com.dnd12th_4.pickitalki.service.channel.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Transactional
@Service
public class QuestionService {

    private final ChannelRepository channelRepository;
    private final QuestionRepository questionRepository;
    private final ChannelService channelService;

    public Long save(Long memberId, String channelId, String content, boolean isAnonymous, String anonymousName) {
        UUID channelUuid = UUID.fromString(channelId);

        validateSaveQuestion(memberId, channelId, channelUuid);

        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 채널을 찾을 수 없습니다. 새 시그널을 생성할 수 없습니다."));
        ChannelMember channelMember = channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("채널에 해당 회원이 존재하지 않습니다. 질문을 생성할 권한이 없습니다."));
        long questionCount = questionRepository.countByChannelUuid(channelUuid);

        Question question = questionRepository.save(
                new Question(channel, channelMember, content, questionCount + 1, isAnonymous,
                        isAnonymous ? anonymousName : channelMember.getMemberCodeName())
        );
        channel.risePoint();

        return question.getId();
    }

    private void validateSaveQuestion(Long memberId, String channelId, UUID channelUuid) {
        if (questionRepository.findTodayQuestion(channelUuid).isPresent()) {
            throw new IllegalStateException("이미 오늘의 질문이 존재합니다. 질문을 생성할 수 없습니다.");
        }

        ChannelMemberProfileResponse todayQuestioner = channelService.findTodayQuestioner(memberId, channelId);
        if (!todayQuestioner.isTodayQuestioner()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "오늘의 질문자가 아닙니다. 질문을 작성할 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public TodayQuestionResponse findTodayQuestion(Long memberId, String channelId) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다. 오늘의 시그널 정보를 찾을 수 없습니다."));
        validateMemberInChannel(channel, memberId);


        return questionRepository.findTodayQuestion(channelUuid)
                .map(question -> TodayQuestionResponse.builder()
                        .isExist(true)
                        .writer(question.getWriterName())
                        .signalCount(question.getQuestionNumber())
                        .time(formatToKoreanTime(question.getCreatedAt()))
                        .content(question.getContent())
                        .questionId(question.getId())
                        .answerCount(question.getAnswerList().size())
                        .hasRespond(getQuestionHasRespond(question, memberId))
                        .build())
                .orElseGet(() -> TodayQuestionResponse.builder()
                        .isExist(false)
                        .writer(null)
                        .signalCount(questionRepository.findMaxQuestionNumber(channelUuid) + 1)
                        .time(formatToKoreanTime(LocalDateTime.now()))
                        .content(null)
                        .questionId(null)
                        .answerCount(0)
                        .hasRespond(false)
                        .build());
    }

    private boolean getQuestionHasRespond(Question question, Long memberId) {
        return question.getAnswerList().stream()
                .anyMatch(it -> it.getAuthor().getMember().getId().equals(memberId));
    }

    private void validateMemberInChannel(Channel channel, Long memberId) {
        channel.findChannelMemberById(memberId);
    }

    private String formatToKoreanTime(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Transactional(readOnly = true)
    public QuestionShowAllResponse findByChannelId(Long memberId, String channelId, Pageable pageable, QuestionControllerEnums questionFilterEnum) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다. 오늘의 시그널 정보를 찾을 수 없습니다."));
        validateMemberInChannel(channel, memberId);

        Page<Question> questionPage;

        switch (questionFilterEnum) {
            case ALL:
                questionPage = questionRepository.findByChannelUuidAndChannelMembers_Member_IdAndIsDeletedFalse(channelUuid, memberId, pageable);
                break;

            case MY_QUESTIONS:
                questionPage = questionRepository.findByChannelUuidAndWriter_Member_IdAndIsDeletedFalse(channelUuid, memberId,pageable);
                break;

            case OTHERS:
                questionPage = questionRepository.findByChannelUuidAndChannelMembers_Member_IdAndWriter_Member_IdNotAndIsDeletedFalse(channelUuid,memberId, pageable);
                break;

            default:
                throw new IllegalArgumentException("지원하지 않는 questionFilter 값입니다: " + questionFilterEnum);
        }

        return toQuestionShowAllResponse(questionPage);
    }

    private QuestionShowAllResponse toQuestionShowAllResponse(Page<Question> questionPage) {

        List<QuestionResponse> questionResponseList = questionPage.getContent().stream()
                .map(question -> QuestionResponse.builder()
                        .questionId(question.getId())
                        .writerName(question.getWriterName())
                        .writerProfileImage(question.getWriter().getProfileImage())
                        .signalNumber(question.getQuestionNumber())
                        .content(question.getContent())
                        .replyCount(question.getAnswerList().size())
                        .createdAt(question.getCreatedAt().toString())
                        .build()
                ).toList();

        PageParamResponse pageParamResponse = PageParamResponse.builder()
                .currentPage(questionPage.getNumber())
                .size(questionPage.getSize())
                .totalElements(questionPage.getNumberOfElements())
                .totalPages(questionPage.getTotalPages())
                .hasNext(questionPage.hasNext())
                .build();

        return QuestionShowAllResponse.builder()
                .questionResponse(questionResponseList)
                .pageParamResponse(pageParamResponse)
                .build();
    }

    @Transactional(readOnly = true)
    public QuestionOneResponse findQuestionById(Long memberId, Long questionId) {
        Question question = questionRepository.findByIdAndIsDeletedFalse(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문을 찾을 수 없습니다."));
        question.getChannel().findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문을 읽을 수 있는 회원이 아닙니다. 이 질문의 채널에 참여해야합니다."));

        boolean isMySignal = question.getWriter().isSameMember(memberId);
        boolean hasMyAnswer = isHasMyAnswer(memberId, question);

        QuestionResponse questionResponse = QuestionResponse.builder()
                .questionId(questionId)
                .writerName(question.getWriterName())
                .writerProfileImage(question.getWriter().getProfileImage())
                .signalNumber(question.getQuestionNumber())
                .content(question.getContent())
                .replyCount(question.getAnswerList().size())
                .createdAt(question.getCreatedAt().toString())
                .build();

        return QuestionOneResponse.builder()
                .questionResponse(questionResponse)
                .hasMyAnswer(hasMyAnswer)
                .isMySignal(isMySignal)
                .build();
    }

    private  boolean isHasMyAnswer(Long memberId, Question question) {

        return question.getAnswerList().stream()
                 .anyMatch(it -> it.getAuthor().getMember().getId().equals(memberId));
    }

    public QuestionUpdateResponse updateQuestion(Long memberId, Long questionId, String content) {
        Question question = questionRepository.findByIdAndIsDeletedFalse(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문을 찾을 수 없습니다. 질문을 수정할 수 없습니다."));
        if (!question.getWriter().isSameMember(memberId)) {
            throw new IllegalArgumentException("해당 질문을 수정할 권한이 없습니다.");
        }

        question.updateContent(content);

        return QuestionUpdateResponse.builder()
                .questionId(question.getId())
                .build();
    }

    public void deleteQuestion(Long memberId, Long questionId) {
        Question question = questionRepository.findByIdAndIsDeletedFalse(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문을 찾을 수 없습니다. 질문을 삭제할 수 없습니다."));
        if (!question.getWriter().isSameMember(memberId)) {
            throw new IllegalArgumentException("해당 질문을 삭제할 권한이 없습니다.");
        }

        Channel channel = question.getChannel();
        channel.getQuestions().remove(question);

    }

    @Transactional(readOnly = true)
    public QuestionShowAllResponse findQuestionsByMember(Long memberId, Pageable pageable, QuestionControllerEnums questionFilterEnum) {
        Page<Question> questionPage;

        switch (questionFilterEnum) {
            case ALL:
                questionPage = questionRepository.findByChannelMembers_Member_IdAndIsDeletedFalse(memberId, pageable);
                break;

            case MY_QUESTIONS:
                questionPage = questionRepository.findByWriter_Member_IdAndIsDeletedFalse(memberId, pageable);
                break;

            case OTHERS:
                questionPage = questionRepository.findByChannelMembers_Member_IdAndWriter_Member_IdNotAndIsDeletedFalse(memberId, pageable);
                break;

            default:
                throw new IllegalArgumentException("지원하지 않는 questionFilter 값입니다: " + questionFilterEnum);
        }


        return QuestionShowAllResponse.builder()
                .questionResponse(toQuestionResponseList(questionPage))
                .pageParamResponse(Pagination.createPageParamResponse(questionPage))
                .build();
    }

    private List<QuestionResponse> toQuestionResponseList(Page<Question> questionPage) {

        return questionPage.getContent().stream()
                .map(q -> QuestionResponse.builder()
                        .questionId(q.getId())
                        .writerProfileImage(q.getWriter().getProfileImage())
                        .writerName(q.getWriterName())
                        .signalNumber(q.getQuestionNumber())
                        .content(q.getContent())
                        .createdAt(q.getCreatedAt().toString())
                        .build()
                ).toList();
    }
}
