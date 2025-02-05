package com.dnd12th_4.pickitalki.domain.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel,UUID> {

    Optional<Channel> findByUuid(UUID uuid);

    Optional<Channel> findByName(String name);

}
