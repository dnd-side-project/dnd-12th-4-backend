package com.dnd12th_4.pickitalki.domain.channel;

import com.dnd12th_4.pickitalki.domain.BaseEntity;
import com.dnd12th_4.pickitalki.domain.question.Question;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    @OneToMany(mappedBy = "channel", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ChannelMember> channelMembers = new ArrayList<>();

    @OneToMany(mappedBy = "channel")
    private List<Question> questions = new ArrayList<>();

    protected Channel() {}

    public Channel(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public Channel(String name) {
        this(UUID.randomUUID(), name);
    }

    @Override
    public boolean isNew() {
        return isNull(createdAt);
    }

    @Override
    public String getId() {
        return uuid.toString();
    }

    public void joinChannelMember(ChannelMember channelMember){
        validateChannelMember(channelMember);
        channelMembers.add(channelMember);
    }

    private void validateChannelMember(ChannelMember channelMember) {
        if (isNull(channelMember)) {
            throw new IllegalArgumentException("채널의 회원이 존재하지 않습니다.");
        }

        if (channelMembers.contains(channelMember)) {
            throw new IllegalArgumentException("이미 해당 채널에 존재하는 회원입니다.");
        }
    }

    public ChannelMember findChannelMemberById(Long memberId) {
        return channelMembers.stream().filter(channelMember -> channelMember.isSameMember(memberId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 채널에 존재하지 않습니다."));

    }

}
