package io.yar.yar2026.user.domain;

import io.yar.yar2026.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;

    @Column(nullable = false)
    private String refreshToken;

    @Setter
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Builder
    public RefreshToken(String refreshToken, User user) {
        this.refreshToken = refreshToken;
        this.user = user;
    }



}
