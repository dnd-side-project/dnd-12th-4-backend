package com.dnd12th_4.pickitalki.domain.channel;

import com.dnd12th_4.pickitalki.domain.BaseEntity;
import com.dnd12th_4.pickitalki.domain.question.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@Getter
@Table(name = "channels")
@Entity
@SuperBuilder
public class Channel extends BaseEntity implements Persistable<String> {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID uuid;

    @Column(nullable = false, length = 10)
    private String name;

    @OneToMany(mappedBy = "channel")
    private List<ChannelMember> channelMembers = new ArrayList<>();

    @OneToMany(mappedBy = "channel")
    private List<Question> questions = new ArrayList<>();

    protected Channel() {}

    public Channel(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
    }

    public Channel(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    @Override
    public boolean isNew() {
        return isNull(createdAt);
    }

    @Override
    public String getId() {
        return uuid.toString();
    }

    public void makeChannelMember(ChannelMember channelMember){
        if(channelMembers==null){
            channelMembers = new ArrayList<>();
        }

        if(channelMember!=null && !channelMembers.contains(channelMember)){
            channelMembers.add(channelMember);
            channelMember.makeChannel(this);
        }
    }
}
