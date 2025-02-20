package com.dnd12th_4.pickitalki.domain.question;

import com.dnd12th_4.pickitalki.domain.BaseEntity;
import com.dnd12th_4.pickitalki.domain.answer.Answer;
import com.dnd12th_4.pickitalki.domain.channel.Channel;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static io.micrometer.common.util.StringUtils.isBlank;

@Getter
@Entity
@Table(name = "questions" ,uniqueConstraints = {@UniqueConstraint(name = "unique_channel_today_question", columnNames = {"channel_uuid", "created_date"})}
)
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_uuid", nullable = false)
    @JsonIgnore
    private Channel channel;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_member_id", nullable = true)
    private ChannelMember writer;

    @Column(nullable = false, length = 100)
    private String content;

    @Column(name = "question_number", nullable = false)
    private long questionNumber;

    @Column(nullable = false)
    private boolean isAnonymous;

    @Column(nullable = true, length = 10)
    private String anonymousName;

    @Column(nullable = false)
    private LocalDate createdDate;

    @OneToMany(mappedBy = "question",orphanRemoval = true,  fetch = FetchType.LAZY)
    private List<Answer> answerList = new ArrayList<>();

    protected Question() {
    }

    public Question(Long id, Channel channel, ChannelMember writer, String content, long questionNumber, boolean isAnonymous, String anonymousName) {
        if (!writer.getChannel().equals(channel)) {
            throw new IllegalArgumentException("작성자는 해당 채널의 멤버여야 합니다.");
        }
        this.id = id;
        this.channel = channel;
        this.writer = writer;
        this.content = content;
        this.questionNumber = questionNumber;
        this.isAnonymous = isAnonymous;
        this.anonymousName = isAnonymous ? anonymousName : null;
        validateAuthorName(isAnonymous, anonymousName);
    }

    private void validateAuthorName(boolean isAnonymous, String authorName) {
        if (isAnonymous && (isBlank(authorName) || authorName.trim().isEmpty())) {
            throw new IllegalArgumentException("질문을 생성할 수 없습니다. 익명 질문은 반드시 익명 닉네임으로 작성해야 합니다.");
        }

        if (!isAnonymous && !writer.getMemberCodeName().equals(authorName)) {
            throw new IllegalArgumentException("질문을 생성할 수 없습니다. 실명 질문은 반드시 채널의 코드네임으로 작성해야 합니다.");
        }
    }

    public Question(Channel channel, ChannelMember writer, String content, long questionNumber, boolean isAnonymous, String anonymousName) {
        this(null, channel, writer, content, questionNumber, isAnonymous, anonymousName);
    }

    public String getWriterName() {
        if (isAnonymous) {
            return anonymousName;
        }
        return writer.getMemberCodeName();
    }

    public void updateContent(String content) {
        this.content = content;
    }

    @PrePersist
    public void prePersist() {
        if (createdAt != null) {
            this.createdDate = createdAt.toLocalDate();
        } else {
            this.createdDate = LocalDate.now();
        }
    }

    @PreRemove
    private void preRemove() {
        this.writer = null; // ChannelMember 삭제 시 null로 설정
    }
}
