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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static final int LEVEL_GAGE = 100;
    public static final int QUESTION_CREATE_POINT = 10;

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    private UUID uuid;

    @Column(nullable = false, length = 10, unique = true)
    private String name;

    @Column(nullable = false, length = 6, unique = true)
    private String inviteCode;

    @Column(name = "point", nullable = false)
    private int point;

    @OneToMany(mappedBy = "channel", cascade = {PERSIST, MERGE}, fetch = FetchType.LAZY)
    private List<ChannelMember> channelMembers = new ArrayList<>();

    @OneToMany(mappedBy = "channel", fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();

    protected Channel() {}

    public Channel(UUID uuid, String name, int point) {
        this.uuid = uuid;
        this.name = name;
        this.point = point;
        this.inviteCode = InviteCodeGenerator.generateInviteCode(uuid);
    }

    public Channel(String name) {
        this(UUID.randomUUID(), name, 0);
    }

    public void joinChannelMember(ChannelMember channelMember) {
        validateNewChannelMember(channelMember);
        channelMembers.add(channelMember);
    }

    private void validateNewChannelMember(ChannelMember channelMember) {
        if (isNull(channelMember)) {
            throw new IllegalArgumentException("채널의 회원이 존재하지 않습니다.");
        }

        boolean isAlreadyExist = channelMembers.stream()
                .anyMatch(ch -> ch.isSameMember(channelMember.getMember().getId()));
        if (isAlreadyExist) {
            throw new IllegalArgumentException("이미 해당 채널에 존재하는 회원입니다.");
        }
    }

    public void leaveChannel(ChannelMember channelMember) {
        if (!channelMembers.remove(channelMember)) {
            throw new IllegalArgumentException("해당 채널에 존재하지 않는 회원입니다. 탈퇴할 수 없습니다.");
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

    public int getLevel() {
        return (point / LEVEL_GAGE) + 1;
    }

    public int getPoint() {
        return point % LEVEL_GAGE;
    }

    public void risePoint() {
        point += QUESTION_CREATE_POINT;
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
        if (obj == null) return false;

        Channel other = (Channel) obj;
        return Objects.equals(uuid, other.uuid);
    }

    public ChannelMember pickTodayQuestioner() {
        String seed = uuid + "-" + java.time.LocalDate.now();
        int index = Math.abs(getHash(seed)) % channelMembers.size();
        return channelMembers.get(index);
    }

    private int getHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return new BigInteger(1, hashBytes).intValue();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("오늘의 질문자 추첨 실패 - SHA-256 algorithm not found", e);
        }
    }
}
