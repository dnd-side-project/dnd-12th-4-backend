package com.dnd12th_4.pickitalki.domain.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelMemberRepository  extends JpaRepository<ChannelMember,Long> {
    List<ChannelMember> findByMemberId(Long memberId);
}
