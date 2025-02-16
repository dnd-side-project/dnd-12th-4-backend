package com.dnd12th_4.pickitalki.domain.channel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelMemberRepository  extends JpaRepository<ChannelMember,Long> {
    List<ChannelMember> findByMemberId(Long memberId);

    @Query("""
    SELECT cm FROM ChannelMember cm
    JOIN FETCH cm.channel c
    JOIN FETCH c.channelMembers friends
    WHERE cm.member.id = :memberId
""")
    List<ChannelMember> findMeOnChannel(@Param("memberId") Long memberId);

    Page<ChannelMember> findByMemberId(Long memberId, Pageable pageable);
}
