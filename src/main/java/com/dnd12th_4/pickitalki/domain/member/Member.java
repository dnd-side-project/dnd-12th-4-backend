package com.dnd12th_4.pickitalki.domain.member;

import com.dnd12th_4.pickitalki.domain.BaseEntity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Persistable;

import static java.util.Objects.isNull;

@Getter
@Setter
@SuperBuilder
@Entity
@Table(name= "member")
public class Member extends BaseEntity  implements Persistable<Long> {

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

    @Override
    public boolean isNew() {
        return isNull(createdAt);
    }
}
