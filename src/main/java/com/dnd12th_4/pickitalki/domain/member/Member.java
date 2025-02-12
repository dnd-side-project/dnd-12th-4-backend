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

import static io.micrometer.common.util.StringUtils.isBlank;

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
        if (isBlank(nickName)) {
            throw new IllegalArgumentException("이름을 변경할 수 없습니다. 1글자 이상의 이름만 설정가능합니다.");
        }
        this.nickName = nickName;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setProfileImageUrl(String imageUrl) {
        if (isBlank(imageUrl)) {
            throw new IllegalArgumentException("프로필 이미지를 변경할 수 없습니다. 유효하지 않은 이미지입니다.");
        }
        this.profileImageUrl = imageUrl;
    }
}
