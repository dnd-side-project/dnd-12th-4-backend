package com.dnd12th_4.pickitalki.domain.Channel;

import com.dnd12th_4.pickitalki.domain.question.TodayQuestion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Table(name = "channels")
@Entity
public class Channel {

    @Id
    @Column(columnDefinition = "BINARY(16)") // ✅ UUID를 BINARY(16)로 저장하여 성능 최적화
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @JdbcTypeCode(SqlTypes.BINARY) // Hibernate 6+ 에서 UUID를 BINARY로 저장하는 설정
    private UUID uuid;

    @Column(nullable = false, length = 10)
    private String name;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChannelMember> channelMembers = new ArrayList<>();

    @OneToOne(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private TodayQuestion todayQuestion;

    protected Channel() {}

    public Channel(String name) {
        this.uuid = UUID.randomUUID();
        this.name = name;
    }

}
