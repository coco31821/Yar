package io.yar.yar2026.user.repository;

import io.yar.yar2026.user.domain.RefreshToken;
import io.yar.yar2026.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RefreshTokenRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("findByToken - refreshToken 문자열로 RefreshToken을 조회한다")
    void findByToken_success() {
        // given
        User user = saveUser("test@test.com", "테스터");

        RefreshToken refreshToken = RefreshToken.create(
                user,
                "refresh-token-1",
                LocalDateTime.now().plusDays(14)
        );

        refreshTokenRepository.save(refreshToken);

        // when
        Optional<RefreshToken> foundRefreshToken =
                refreshTokenRepository.findByToken("refresh-token-1");

        // then
        assertThat(foundRefreshToken).isPresent();
        assertThat(foundRefreshToken.get().getToken()).isEqualTo("refresh-token-1");
        assertThat(foundRefreshToken.get().getUser().getUserId()).isEqualTo(user.getUserId());
    }

    @Test
    @DisplayName("findByToken - 존재하지 않는 refreshToken이면 Optional.empty를 반환한다")
    void findByToken_empty() {
        // given

        // when
        Optional<RefreshToken> foundRefreshToken =
                refreshTokenRepository.findByToken("not-exist-token");

        // then
        assertThat(foundRefreshToken).isEmpty();
    }

    @Test
    @DisplayName("deleteAllByUser_UserId - 특정 유저의 refreshToken을 모두 삭제한다")
    void deleteAllByUser_UserId_success() {
        // given
        User user1 = saveUser("user1@test.com", "유저1");
        User user2 = saveUser("user2@test.com", "유저2");

        refreshTokenRepository.save(
                RefreshToken.create(
                        user1,
                        "user1-refresh-token-1",
                        LocalDateTime.now().plusDays(14)
                )
        );

        refreshTokenRepository.save(
                RefreshToken.create(
                        user1,
                        "user1-refresh-token-2",
                        LocalDateTime.now().plusDays(14)
                )
        );

        refreshTokenRepository.save(
                RefreshToken.create(
                        user2,
                        "user2-refresh-token-1",
                        LocalDateTime.now().plusDays(14)
                )
        );

        // when
        refreshTokenRepository.deleteAllByUser_UserId(user1.getUserId());
        refreshTokenRepository.flush();

        // then
        assertThat(refreshTokenRepository.findByToken("user1-refresh-token-1")).isEmpty();
        assertThat(refreshTokenRepository.findByToken("user1-refresh-token-2")).isEmpty();
        assertThat(refreshTokenRepository.findByToken("user2-refresh-token-1")).isPresent();
    }

    @Test
    @DisplayName("RefreshToken은 한 유저가 여러 개 가질 수 있다")
    void user_can_have_many_refreshTokens() {
        // given
        User user = saveUser("test@test.com", "테스터");

        RefreshToken refreshToken1 = RefreshToken.create(
                user,
                "refresh-token-1",
                LocalDateTime.now().plusDays(14)
        );

        RefreshToken refreshToken2 = RefreshToken.create(
                user,
                "refresh-token-2",
                LocalDateTime.now().plusDays(14)
        );

        // when
        refreshTokenRepository.save(refreshToken1);
        refreshTokenRepository.save(refreshToken2);

        // then
        assertThat(refreshTokenRepository.findByToken("refresh-token-1")).isPresent();
        assertThat(refreshTokenRepository.findByToken("refresh-token-2")).isPresent();
    }

    private User saveUser(String email, String nickname) {
        User user = User.builder()
                .email(email)
                .password("encoded-password")
                .nickname(nickname)
                .build();

        return userRepository.save(user);
    }
}