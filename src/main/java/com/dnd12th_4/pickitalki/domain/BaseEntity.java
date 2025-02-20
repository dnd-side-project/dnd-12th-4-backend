package com.dnd12th_4.pickitalki.domain;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
        this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();  // ✅ LocalDateTime으로 변환
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();  // ✅ LocalDateTime으로 변환
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public void softRestore() {
        this.isDeleted = false;
    }
}



