package com.dnd12th_4.pickitalki.domain.member;



import com.dnd12th_4.pickitalki.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tutorial")
public class Tutorial extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(columnDefinition = "varchar(10)",nullable = false)
    @Enumerated(EnumType.STRING)
    private TutorialStatus status;

    public void setStatus(TutorialStatus status) {
        this.status = status;
    }
}
