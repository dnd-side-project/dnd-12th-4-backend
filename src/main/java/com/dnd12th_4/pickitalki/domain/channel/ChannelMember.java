package com.dnd12th_4.pickitalki.domain.channel;


import com.dnd12th_4.pickitalki.domain.BaseEntity;
import com.dnd12th_4.pickitalki.domain.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

    public static final int LEVEL_GAGE = 100;
    public static final int QUESTION_CREATE_POINT = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_uuid", nullable = false)
    @JsonIgnore
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)

    @JoinColumn(name = "member_id" ,nullable = false)
    @JsonIgnore
    private Member member;

    @Column(name = "member_code_name", nullable = true)
    private String memberCodeName;


    @Column(name = "profile_image", nullable=true)
    private String profileImage;

    @Column(name = "is_using_default_profile", nullable = false)
    private boolean isUsingDefaultProfile = true;


    @Column(name = "point", nullable = false)
    private int point;


    @Column(columnDefinition = "varchar(10)", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    protected ChannelMember() {
    }

    public ChannelMember(Channel channel, Member member, String memberCodeName, int point, Role role) {
        validateChannel(channel);
        this.channel = channel;
        this.member = member;
        this.memberCodeName = memberCodeName;
        this.isUsingDefaultProfile = true;
        this.profileImage = null;

        this.point = point;

        this.role = role;
    }

    public ChannelMember(Channel channel, Member member, String memberCodeName, Role role) {
        this(channel, member, memberCodeName, 0, role);
    }

    public ChannelMember(Channel channel, Member member, Role role) {
        this(channel, member, null, 0, role);
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

    public boolean isSameMember(Long memberId) {
        return Objects.equals(memberId, this.member.getId());
    }

    public int getLevel() {
        return (point / LEVEL_GAGE) + 1;
    }

    public int getPoint() {
        return point % LEVEL_GAGE;
    }

    public void risePoint() {
        point += QUESTION_CREATE_POINT;
    }

    public String getProfileImage() {
        if (isUsingDefaultProfile) {
            return member.getProfileImageUrl();
        }
        return profileImage;
    }

    public void setCustomProfileImage(String profileImage) {
        this.profileImage = profileImage;
        this.isUsingDefaultProfile = false;
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
