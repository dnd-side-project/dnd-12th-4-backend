package com.dnd12th_4.pickitalki.domain.channel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChannelMemberRepository  extends JpaRepository<ChannelMember,Long> {

    @Query("""
    SELECT cm FROM ChannelMember cm
    JOIN FETCH cm.channel c
    JOIN FETCH c.channelMembers friends
    WHERE cm.member.id = :memberId
""")
    List<ChannelMember> findMeOnChannelAndIsDeletedFalse(@Param("memberId") Long memberId);

    Page<ChannelMember> findByMemberIdAndIsDeletedFalse(Long memberId, Pageable pageable);

    @Query("""
        SELECT cm FROM ChannelMember cm
        WHERE cm.channel.uuid = :channelUuid AND cm.isDeleted = false
    """)
    Page<ChannelMember> findByChannelUuidAndIsDeletedFalse(@Param("channelUuid") UUID channelUuid, Pageable pageable);
}
