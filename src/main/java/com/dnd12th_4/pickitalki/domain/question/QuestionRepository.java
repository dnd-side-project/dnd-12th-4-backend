package com.dnd12th_4.pickitalki.domain.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {

    @Query("SELECT COUNT(q) FROM Question q WHERE q.channel.uuid = :channelUuid And q.isDeleted = false")
    long countByChannelUuid(@Param("channelUuid") UUID channelUuid);

    @Query("SELECT q FROM Question q WHERE q.channel.uuid = :channelUuid AND q.createdDate = CURRENT_DATE AND q.isDeleted = false")
    Optional<Question> findTodayQuestion(@Param("channelUuid") UUID channelUuid);

    @Query("SELECT COALESCE(MAX(q.questionNumber), 0) FROM Question q WHERE q.channel.uuid = :channelUuid And q.isDeleted = false")
    long findMaxQuestionNumber(@Param("channelUuid") UUID channelUuid);

    Optional<Question> findByIdAndIsDeletedFalse(Long id);

    Page<Question> findByChannelUuidAndIsDeletedFalseOrderByCreatedAtAsc(UUID channelUuid, Pageable pageable);

}

