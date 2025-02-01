package com.dnd12th_4.pickitalki.domain.member;

import com.dnd12th_4.pickitalki.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.springframework.data.domain.Persistable;

import static java.util.Objects.isNull;


@Getter
@Table(name = "members")
@Entity
public class Member extends BaseEntity implements Persistable<String> {

    @Id
    private String id;

    @Column(nullable = false, length = 10)
    private String name;

    @Column(nullable = true)
    private String image;


    protected Member() {
    }

    public Member(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    @Override
    public boolean isNew() {
        return isNull(createdAt);
    }
}
