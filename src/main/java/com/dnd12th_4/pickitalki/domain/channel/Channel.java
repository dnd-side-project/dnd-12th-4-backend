package com.dnd12th_4.pickitalki.domain.channel;

import com.dnd12th_4.pickitalki.domain.BaseEntity;
import com.dnd12th_4.pickitalki.domain.question.Question;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static jakarta.persistence.CascadeType.MERGE;
import static jakarta.persistence.CascadeType.PERSIST;
import static java.util.Objects.hash;
import static java.util.Objects.isNull;

@Getter
@Table(name = "channels")
@Entity
@SuperBuilder
public class Channel extends BaseEntity implements Persistable<String> {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    private UUID uuid;

    @Column(nullable = false, length = 10, unique = true)
    private String name;

    @Column(nullable = false, length = 6, unique = true)
    private String inviteCode;

    @OneToMany(mappedBy = "channel", cascade = {PERSIST, MERGE}, fetch = FetchType.LAZY)
    private List<ChannelMember> channelMembers = new ArrayList<>();

    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();

    protected Channel() {}

    public Channel(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.inviteCode = InviteCodeGenerator.generateInviteCode(uuid);
    }

    public Channel(String name) {
        this(UUID.randomUUID(), name);
    }

    public void joinChannelMember(ChannelMember channelMember){
        validateChannelMember(channelMember);
        channelMembers.add(channelMember);
    }

    private void validateChannelMember(ChannelMember channelMember) {
        if (isNull(channelMember)) {
            throw new IllegalArgumentException("채널의 회원이 존재하지 않습니다.");
        }

        boolean isAlreadyExist = channelMembers.stream()
                .anyMatch(ch -> ch.isSameMember(channelMember.getMember().getId()));
        if (isAlreadyExist) {
            throw new IllegalArgumentException("이미 해당 채널에 존재하는 회원입니다.");
        }
    }

    public Optional<ChannelMember> findChannelMemberById(Long memberId) {
        return channelMembers.stream()
                .filter(channelMember -> channelMember.isSameMember(memberId))
                .findFirst();
    }

    public List<ChannelMember> getChannelMembers() {
        return Collections.unmodifiableList(channelMembers);
    }

    @Override
    public boolean isNew() {
        return isNull(createdAt);
    }

    @Override
    public String getId() {
        return uuid.toString();
    }

    @Override
    public int hashCode() {
        return hash(uuid);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Channel other = (Channel) obj;
        return Objects.equals(uuid, other.uuid);
    }
}
