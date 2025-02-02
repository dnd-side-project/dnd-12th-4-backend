package com.dnd12th_4.pickitalki.domain.channel;


import com.dnd12th_4.pickitalki.domain.BaseEntity;
import com.dnd12th_4.pickitalki.domain.member.Member;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@Table(name = "channel_members")
@Entity
@SuperBuilder
public class ChannelMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(nullable = false)
    private Channel channel;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(nullable = false)
    private Member member;

    @Column(nullable=true)
    private String memberCodeName;

    @Column(columnDefinition = "varchar(50)", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    protected ChannelMember() {
    }

    public ChannelMember(Long id, Channel channel, Member member, Role role) {
        this.id = id;
        this.channel = channel;
        this.member = member;
        this.role = role;
    }

    public void setMemberCodeName(String memberCodeName) {
        this.memberCodeName = memberCodeName;
    }

    public void makeMember(Member member){
        if(member==null)return;

        if(this.member!=member){
            this.member=member;
            member.makeChannelMember(this);
        }
    }

    public void makeChannel(Channel channel){
        if(channel ==null)return;

        if(this.channel!= channel){
            this.channel = channel;
            channel.makeChannelMember(this);
        }
    }

}
