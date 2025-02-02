package com.dnd12th_4.pickitalki.domain.channel;


import com.dnd12th_4.pickitalki.domain.BaseEntity;
import com.dnd12th_4.pickitalki.domain.member.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Table(name = "channel_members")
@Entity
public class ChannelMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member member;

    private Role role;

    protected ChannelMember() {
    }

    public ChannelMember(Long id, Channel channel, Member member, Role role) {
        this.id = id;
        this.channel = channel;
        this.member = member;
        this.role = role;
    }

}
