package com.dnd12th_4.pickitalki.domain.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question,Long> {

    @Query("SELECT COUNT(q) FROM Question q WHERE q.channel.uuid = :channelUuid")
    long countByChannelUuid(@Param("channelUuid") UUID channelUuid);

    @Query("SELECT q FROM Question q WHERE q.channel.uuid = :channelUuid AND q.createdDate = CURRENT_DATE")
    Optional<Question> findTodayQuestion(@Param("channelUuid") UUID channelUuid);

}
