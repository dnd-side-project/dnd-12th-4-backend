package com.dnd12th_4.pickitalki.domain;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    protected LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    protected LocalDateTime updatedAt;

    @Column(nullable = false, name = "is_deleted")
    protected boolean isDeleted = false;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul")); // KST 기준으로 저장
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now(java.time.ZoneId.of("Asia/Seoul")); // KST 기준으로 저장
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public void softRestore() {
        this.isDeleted = false;
    }
}



