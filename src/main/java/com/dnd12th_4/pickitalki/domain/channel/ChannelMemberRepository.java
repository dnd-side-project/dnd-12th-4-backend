package com.dnd12th_4.pickitalki.domain.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ChannelMemberRepository  extends JpaRepository<ChannelMember,Long> {


}
