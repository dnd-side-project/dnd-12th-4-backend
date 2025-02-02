package com.dnd12th_4.pickitalki.domain.question;

import com.dnd12th_4.pickitalki.domain.BaseEntity;
import com.dnd12th_4.pickitalki.domain.channel.Channel;
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
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(name = "questions", uniqueConstraints = {
        @UniqueConstraint(name = "unique_channel_today_question", columnNames = {"channel_uuid", "created_date"})
})
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_uuid", nullable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private Member author;

    @Column(nullable = false, length = 100)
    private String content;

    @Column(nullable = false)
    private boolean isTodayQuestion;

    @Column(nullable = false)
    private boolean isAnonymous;

    @Column(nullable = true, length = 10)
    private String anonymousName;

    @Column(nullable = false)
    private LocalDate createdDate;

    protected Question() {
    }

    public Question(Long id, Channel channel, Member author, String content, boolean isTodayQuestion,
                    boolean isAnonymous, String anonymousName, LocalDate createdDate) {
        this.id = id;
        this.channel = channel;
        this.author = author;
        this.content = content;
        this.isTodayQuestion = isTodayQuestion;
        this.isAnonymous = isAnonymous;
        this.createdDate = createdDate;

        if (isAnonymous) {
            this.anonymousName = anonymousName;
        } else {
            this.anonymousName = null;
        }
    }

    public Question(Channel channel, Member author, String content, boolean isAnonymous, String anonymousName) {
        this(null, channel, author, content, false, isAnonymous, anonymousName, LocalDate.now());
    }

}
