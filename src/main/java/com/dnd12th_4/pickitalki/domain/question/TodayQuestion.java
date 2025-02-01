package com.dnd12th_4.pickitalki.domain.question;

import com.dnd12th_4.pickitalki.domain.BaseEntity;
import com.dnd12th_4.pickitalki.domain.channel.Channel;
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
public class TodayQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = true)
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

    public TodayQuestion(Channel channel) {
        this.channel = channel;
        this.createdDate = LocalDate.now();
    }
}

