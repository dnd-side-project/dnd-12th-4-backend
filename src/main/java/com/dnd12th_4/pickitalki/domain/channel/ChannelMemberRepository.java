package com.dnd12th_4.pickitalki.domain.channel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChannelMemberRepository  extends JpaRepository<ChannelMember,Long> {

}
