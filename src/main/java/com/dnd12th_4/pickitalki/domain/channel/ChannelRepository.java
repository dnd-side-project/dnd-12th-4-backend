package com.dnd12th_4.pickitalki.domain.channel;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository extends JpaRepository<Channel,UUID> {

    Optional<Channel> findByUuid(UUID uuid);

}
