package io.yar.yar2026.profile.domain;

import io.yar.yar2026.common.BaseEntity;
import io.yar.yar2026.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "profiles")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private int level;

    @Column(nullable = false)
    private long exp;

    @Column(nullable = false)
    private long totalPlaySeconds;

    @Builder
    public Profile(User user, int level, long exp, long totalPlaySeconds) {
        this.user = user;
        this.level = level;
        this.exp = exp;
        this.totalPlaySeconds = totalPlaySeconds;
    }

    public static Profile createDefault(User user) {
        return Profile.builder()
                .user(user)
                .level(1)
                .exp(0)
                .totalPlaySeconds(0)
                .build();
    }
}
