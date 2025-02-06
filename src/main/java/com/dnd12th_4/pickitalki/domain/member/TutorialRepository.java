package com.dnd12th_4.pickitalki.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TutorialRepository extends JpaRepository<Tutorial,Long> {

    @Query("SELECT t.status from Tutorial t where t.memberId =:memberId")
    Optional<TutorialStatus> findStatusByMemberId(@Param("memberId")Long memberId);

    Optional<Tutorial> findByMemberId(Long memberId);

}
