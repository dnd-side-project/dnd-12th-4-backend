package com.dnd12th_4.pickitalki.domain.question;

import com.dnd12th_4.pickitalki.domain.Channel.Channel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "today_questions", uniqueConstraints = {
        @UniqueConstraint(name = "unique_channel_today_question", columnNames = {"channel_id", "created_date"})
})
public class TodayQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false, unique = true)
    private Channel channel;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = true)  // 오늘의 질문이 없을 수도 있음
    private Question question;

    @Column(nullable = false)
    private LocalDate createdDate;

    protected TodayQuestion() {
    }

    public TodayQuestion(Channel channel, Question question) {
        this.channel = channel;
        this.question = question;
        this.createdDate = LocalDate.now();
    }

    public TodayQuestion(Channel channel) {  // 질문이 없는 경우
        this.channel = channel;
        this.createdDate = LocalDate.now();
    }
}

