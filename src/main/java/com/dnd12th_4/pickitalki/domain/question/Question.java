package com.dnd12th_4.pickitalki.domain.question;

import com.dnd12th_4.pickitalki.domain.Channel.Channel;
import com.dnd12th_4.pickitalki.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isAnonymous; // 익명 여부 추가

    @Column(nullable = false)
    private boolean isTodayQuestion; // 익명 여부 추가

    @Column(nullable = true, length = 10) // 익명 닉네임 (최대 10자)
    private String anonymousName;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Question() {
    }

    public Question(Channel channel, Member author, String content, boolean isAnonymous, String anonymousName) {
        this.channel = channel;
        this.author = author;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.createdAt = LocalDateTime.now();

        if (isAnonymous) {
            this.anonymousName = anonymousName; // 익명 모드일 경우 설정한 이름 저장
        } else {
            this.anonymousName = null; // 익명 모드가 아니면 null 처리
        }
    }

}
