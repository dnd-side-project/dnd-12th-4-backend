package com.dnd12th_4.pickitalki.service.question;

import com.dnd12th_4.pickitalki.controller.question.dto.QuestionResponse;

import com.dnd12th_4.pickitalki.controller.question.dto.QuestionUpdateResponse;

import com.dnd12th_4.pickitalki.controller.question.dto.TodayQuestionResponse;
import com.dnd12th_4.pickitalki.domain.channel.Channel;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
import com.dnd12th_4.pickitalki.domain.channel.ChannelRepository;
import com.dnd12th_4.pickitalki.domain.question.Question;
import com.dnd12th_4.pickitalki.domain.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
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

    public Long save(Long memberId, String channelId, String content, boolean isAnonymous, String anonymousName) {
        UUID channelUuid = UUID.fromString(channelId);

        if (questionRepository.findTodayQuestion(channelUuid).isPresent()) {
            throw new IllegalStateException("이미 오늘의 질문이 존재합니다. 질문을 생성할 수 없습니다.");
        }

        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 채널을 찾을 수 없습니다. 새 시그널을 생성할 수 없습니다."));

        ChannelMember channelMember = channel.findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("채널에 해당 회원이 존재하지 않습니다. 질문을 생성할 권한이 없습니다."));
        long questionCount = questionRepository.countByChannelUuid(channelUuid);

        Question question = questionRepository.save(
                new Question(channel, channelMember, content, questionCount + 1, isAnonymous,
                        isAnonymous ? anonymousName : channelMember.getMemberCodeName())
        );
        channelMember.risePoint();

        return question.getId();
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
                        .build())
                .orElseGet(() -> TodayQuestionResponse.builder()
                        .isExist(false)
                        .writer(null)
                        .signalCount(questionRepository.findMaxQuestionNumber(channelUuid) + 1)
                        .time(formatToKoreanTime(LocalDateTime.now()))
                        .content(null)
                        .build());
    }

    private void validateMemberInChannel(Channel channel, Long memberId) {
        channel.findChannelMemberById(memberId);
    }

    private String formatToKoreanTime(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> findByChannelId(Long memberId, String channelId) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다. 오늘의 시그널 정보를 찾을 수 없습니다."));
        validateMemberInChannel(channel, memberId);

        List<Question> questions = questionRepository.findByChannelUuidOrderByCreatedAtAsc(channelUuid);

        return questions.stream()
                .map(question -> new QuestionResponse(
                        question.getWriterName(),
                        question.getQuestionNumber(),
                        question.getContent(),
                        question.getCreatedAt().toString()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public QuestionResponse findQuestionById(Long memberId, Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문을 찾을 수 없습니다."));
        question.getChannel().findChannelMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문을 읽을 수 있는 회원이 아닙니다. 이 질문의 채널에 참여해야합니다."));

        return QuestionResponse.builder()
                .writerName(question.getWriterName())
                .signalNumber(question.getQuestionNumber())
                .content(question.getContent())
                .createdAt(question.getCreatedAt().toString())
                .build();
    }

    public QuestionUpdateResponse updateQuestion(Long memberId, Long questionId, String content) {
        Question question = questionRepository.findById(questionId)
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
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문을 찾을 수 없습니다. 질문을 삭제할 수 없습니다."));
        if (!question.getWriter().isSameMember(memberId)) {
            throw new IllegalArgumentException("해당 질문을 삭제할 권한이 없습니다.");
        }

        questionRepository.delete(question);
    }
}
