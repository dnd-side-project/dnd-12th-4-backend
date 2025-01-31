package com.dnd12th_4.pickitalki.domain.answer;

import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.question.Question;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "answers")
public class Answer {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member author;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private boolean isAnonymous; // 익명 여부

    @Column(length = 30)
    private String anonymousName; // 익명 닉네임

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected Answer() {
    }

    public Answer(Question question, Member author, String content, boolean isAnonymous, String anonymousName) {
        this.question = question;
        this.author = author;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.anonymousName = isAnonymous ? anonymousName : null;
    }
}
