package com.dnd12th_4.pickitalki.domain.answer;

import com.dnd12th_4.pickitalki.domain.BaseEntity;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.dnd12th_4.pickitalki.domain.question.Question;
import com.dnd12th_4.pickitalki.presentation.error.ErrorCode;
import com.dnd12th_4.pickitalki.presentation.exception.ApiException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;

@Getter
@Entity
@SuperBuilder
@Table(name = "answers")
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,cascade = {PERSIST, MERGE})
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_member_id", nullable = false)
    @JsonIgnore
    private ChannelMember author;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private boolean isAnonymous;

    @Column(length = 30)
    private String anonymousName;


    protected Answer() {
    }

    public Answer(Question question, ChannelMember author, String content, boolean isAnonymous, String anonymousName) {
        makeQuestion(question); // 연관관계 설정
        this.author = author;
        this.content = content;
        this.isAnonymous = isAnonymous;
        this.anonymousName = isAnonymous ? anonymousName : null;
    }
    public void setContent(String content) {
        this.content = content;
    }


    public void makeQuestion(Question question){
       if(question == null){
           throw new ApiException(ErrorCode.BAD_REQUEST,"question은 null이 될 수 없습니다.");
       }

       if(this.question !=null){
           this.question.getAnswerList().remove(this);
       }

       this.question = question;
       if(!question.getAnswerList().contains(this)){
           question.getAnswerList().add(this);
       }
    }

}
