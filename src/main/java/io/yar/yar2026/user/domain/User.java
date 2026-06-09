package io.yar.yar2026.user.domain;

import io.yar.yar2026.common.BaseEntity;
import io.yar.yar2026.user.constants.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String profileImageUrl;

    private String provider;

    private String status;

    private LocalDateTime lastLoginAt;

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;

        this.role = Role.USER;
        this.profileImageUrl = null;
        this.provider = "LOCAL";
        this.status = "ACTIVE";
        this.lastLoginAt = null;
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
