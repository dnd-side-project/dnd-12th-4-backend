package com.dnd12th_4.pickitalki.domain.channel;


import com.dnd12th_4.pickitalki.domain.BaseEntity;
import com.dnd12th_4.pickitalki.domain.member.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

import static java.util.Objects.hash;
import static java.util.Objects.isNull;

@Getter
@Table(name = "channel_members")
@Entity
@SuperBuilder
public class ChannelMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_uuid", nullable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "member_id" ,nullable = false)
    private Member member;

    @Column(nullable=true)
    private String memberCodeName;

    @Column(columnDefinition = "varchar(50)", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    protected ChannelMember() {
    }

    public ChannelMember(Channel channel, Member member, Role role) {
        validateChannel(channel);
        this.channel = channel;
        this.member = member;
        this.role = role;
    }

    private void validateChannel(Channel channel) {
        if (isNull(channel)) {
            throw new IllegalArgumentException("참여할 채널이 존재하지 않습니다.");
        }

        if (Objects.equals(this.channel, channel)) {
            throw new IllegalArgumentException("이미 해당 채널에 참여한 회원입니다.");
        }
    }

    public void setMemberCodeName(String memberCodeName) {
        this.memberCodeName = memberCodeName;
    }

    @Override
    public int hashCode() {
        return hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ChannelMember other = (ChannelMember) obj;
        return Objects.equals(id, other.id);
    }
}
