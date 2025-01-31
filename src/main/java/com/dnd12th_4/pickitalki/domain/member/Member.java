package com.dnd12th_4.pickitalki.domain.member;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

import java.util.ArrayList;


@Getter
@Table(name = "members")
@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 10)
    private String name;

    protected Member() {
    }

    public Member(Long id, String email, String name) {
        this.id = id;
        this.email = email;
        this.name = name;
    }
}
