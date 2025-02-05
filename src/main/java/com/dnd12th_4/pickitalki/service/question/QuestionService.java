package com.dnd12th_4.pickitalki.service.question;

import com.dnd12th_4.pickitalki.controller.question.TodayQuestionResponse;
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

        ChannelMember channelMember = channel.findChannelMemberById(memberId);

        Question question = questionRepository.save(
                new Question(channel, channelMember, content, isAnonymous,
                        isAnonymous ? anonymousName : channelMember.getMemberCodeName())
        );

        return question.getId();
    }

    public TodayQuestionResponse findTodayQuestion(Long memberId, String channelId) {
        UUID channelUuid = UUID.fromString(channelId);
        Channel channel = channelRepository.findByUuid(channelUuid)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다. 오늘의 시그널 정보를 찾을 수 없습니다."));

        validateMemberInChannel(channel, memberId);
        long signalCount = questionRepository.countByChannelUuid(channelUuid);

        return questionRepository.findTodayQuestion(channelUuid)
                .map(question -> TodayQuestionResponse.builder()
                        .isExist(true)
                        .signalCount(signalCount)
                        .time(formatToKoreanTime(question.getCreatedAt()))
                        .content(question.getContent())
                        .build())
                .orElseGet(() -> TodayQuestionResponse.builder()
                        .isExist(false)
                        .signalCount(signalCount + 1)
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
}
