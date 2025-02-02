package com.dnd12th_4.pickitalki.domain.member;

import com.dnd12th_4.pickitalki.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
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

    @Column(length = 50, nullable = false)
    private String nickName;

    @Column(nullable = true)
    private String profileImageUrl;

    @Column(nullable = true)
    private String refreshToken;

    protected Member() {
        super();

    }

    public Member(Long id, String name, String image) {
        this.id = id;
        this.nickName = name;
        this.profileImageUrl = image;
    }

}
