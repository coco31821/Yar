package io.yar.yar2026.user.repository;

import io.yar.yar2026.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("existsByEmail - 해당 이메일을 가진 유저가 존재하면 true를 반환한다")
    void existsByEmail_true() {
        // given
        User user = User.builder()
                .email("test@test.com")
                .password("encoded-password")
                .nickname("테스터")
                .build();

        userRepository.save(user);

        // when
        boolean exists = userRepository.existsByEmail("test@test.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail - 해당 이메일을 가진 유저가 없으면 false를 반환한다")
    void existsByEmail_false() {
        // when
        boolean exists = userRepository.existsByEmail("none@test.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findByEmail - 해당 이메일을 가진 유저를 조회한다")
    void findByEmail_success() {
        // given
        User user = User.builder()
                .email("test@test.com")
                .password("encoded-password")
                .nickname("테스터")
                .build();

        userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findByEmail("test@test.com");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test@test.com");
        assertThat(foundUser.get().getNickname()).isEqualTo("테스터");
    }

    @Test
    @DisplayName("findByEmail - 해당 이메일을 가진 유저가 없으면 Optional.empty를 반환한다")
    void findByEmail_empty() {
        // when
        Optional<User> foundUser = userRepository.findByEmail("none@test.com");

        // then
        assertThat(foundUser).isEmpty();
    }
}