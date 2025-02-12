package com.dnd12th_4.pickitalki.domain.member;

import com.dnd12th_4.pickitalki.domain.BaseEntity;
import com.dnd12th_4.pickitalki.domain.channel.ChannelMember;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
@Entity
@Table(name = "members")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long kakaoId;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 50, nullable = true)
    private String nickName;

    @Column(nullable = true)
    private String profileImageUrl;

    @Column(nullable = true)
    private String refreshToken;

    @OneToMany(mappedBy = "member",fetch = FetchType.LAZY)
    private List<ChannelMember> channelMembers = new ArrayList<>();

    protected Member() {
        super();

    }

    public Member(Long id, String name, String image) {
        this.id = id;
        this.nickName = name;
        this.profileImageUrl = image;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

}
