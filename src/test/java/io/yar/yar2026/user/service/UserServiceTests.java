package io.yar.yar2026.user.service;

import io.yar.yar2026.common.config.security.TokenProvider;
import io.yar.yar2026.user.constants.Role;
import io.yar.yar2026.user.domain.RefreshToken;
import io.yar.yar2026.user.domain.User;
import io.yar.yar2026.user.dto.*;
import io.yar.yar2026.user.exception.DuplicateEmailException;
import io.yar.yar2026.user.exception.InvalidRefreshTokenException;
import io.yar.yar2026.user.exception.LoginFailedException;
import io.yar.yar2026.user.repository.RefreshTokenRepository;
import io.yar.yar2026.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("회원가입 성공 - 이메일이 중복되지 않으면 비밀번호를 암호화하고 유저를 저장한다")
    void signup_success() {
        // given
        SignupRequest request = new SignupRequest(
                "test@test.com",
                "password123",
                "테스터"
        );

        given(userRepository.existsByEmail("test@test.com"))
                .willReturn(false);

        given(passwordEncoder.encode("password123"))
                .willReturn("encoded-password");

        given(userRepository.save(any(User.class)))
                .willAnswer(invocation -> {
                    User savedUser = invocation.getArgument(0);
                    ReflectionTestUtils.setField(savedUser, "userId", 1L);
                    return savedUser;
                });

        // when
        UserCreateResponse response = userService.signup(request);

        // then
        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("test@test.com");
        assertThat(response.nickname()).isEqualTo("테스터");
        assertThat(response.role()).isEqualTo(Role.USER);

        then(userRepository).should().existsByEmail("test@test.com");
        then(passwordEncoder).should().encode("password123");
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일이면 DuplicateEmailException이 발생한다")
    void signup_fail_duplicateEmail() {
        // given
        SignupRequest request = new SignupRequest(
                "test@test.com",
                "password123",
                "테스터"
        );

        given(userRepository.existsByEmail("test@test.com"))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessage("이미 사용 중인 이메일입니다.");

        then(passwordEncoder).should(never()).encode(anyString());
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공 - 이메일과 비밀번호가 일치하면 accessToken을 발급하고 refreshToken을 저장한다")
    void login_success() {
        // given
        User user = createUser(
                1L,
                "test@test.com",
                "encoded-password",
                "테스터"
        );

        LoginRequest request = new LoginRequest(
                "test@test.com",
                "password123"
        );

        given(userRepository.findByEmail("test@test.com"))
                .willReturn(Optional.of(user));

        given(passwordEncoder.matches("password123", "encoded-password"))
                .willReturn(true);

        given(tokenProvider.issueAccessToken(1L, Role.USER))
                .willReturn("access-token");

        given(tokenProvider.getAccessTokenExpirationSeconds())
                .willReturn(900L);

        given(tokenProvider.getRefreshTokenExpirationSeconds())
                .willReturn(1209600L);

        given(refreshTokenRepository.save(any(RefreshToken.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        TokenResponse response = userService.login(request);

        // then
        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.accessExpiresInSeconds()).isEqualTo(900L);

        assertThat(user.getLastLoginAt()).isNotNull();

        ArgumentCaptor<RefreshToken> refreshTokenCaptor =
                ArgumentCaptor.forClass(RefreshToken.class);

        then(refreshTokenRepository).should().save(refreshTokenCaptor.capture());

        RefreshToken savedRefreshToken = refreshTokenCaptor.getValue();

        assertThat(savedRefreshToken.getUser()).isSameAs(user);
        assertThat(savedRefreshToken.getToken()).isEqualTo(response.refreshToken());
        assertThat(savedRefreshToken.getExpiredAt()).isAfter(LocalDateTime.now());

        then(userRepository).should().findByEmail("test@test.com");
        then(passwordEncoder).should().matches("password123", "encoded-password");
        then(tokenProvider).should().issueAccessToken(1L, Role.USER);
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일이면 LoginFailedException이 발생한다")
    void login_fail_emailNotFound() {
        // given
        LoginRequest request = new LoginRequest(
                "notfound@test.com",
                "password123"
        );

        given(userRepository.findByEmail("notfound@test.com"))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");

        then(passwordEncoder).should(never()).matches(anyString(), anyString());
        then(refreshTokenRepository).should(never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호가 일치하지 않으면 LoginFailedException이 발생한다")
    void login_fail_wrongPassword() {
        // given
        User user = createUser(
                1L,
                "test@test.com",
                "encoded-password",
                "테스터"
        );

        LoginRequest request = new LoginRequest(
                "test@test.com",
                "wrong-password"
        );

        given(userRepository.findByEmail("test@test.com"))
                .willReturn(Optional.of(user));

        given(passwordEncoder.matches("wrong-password", "encoded-password"))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다.");

        then(tokenProvider).should(never()).issueAccessToken(anyLong(), any(Role.class));
        then(refreshTokenRepository).should(never()).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("토큰 재발급 성공 - refreshToken이 유효하면 새 accessToken과 새 refreshToken을 발급하고 기존 토큰을 rotation 한다")
    void refresh_success() {
        // given
        User user = createUser(
                1L,
                "test@test.com",
                "encoded-password",
                "테스터"
        );

        RefreshToken refreshToken = RefreshToken.create(
                user,
                "old-refresh-token",
                LocalDateTime.now().plusDays(14)
        );

        RefreshRequest request = new RefreshRequest("old-refresh-token");

        given(refreshTokenRepository.findByToken("old-refresh-token"))
                .willReturn(Optional.of(refreshToken));

        given(tokenProvider.issueAccessToken(1L, Role.USER))
                .willReturn("new-access-token");

        given(tokenProvider.getAccessTokenExpirationSeconds())
                .willReturn(900L);

        given(tokenProvider.getRefreshTokenExpirationSeconds())
                .willReturn(1209600L);

        // when
        TokenResponse response = userService.refresh(request);

        // then
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotEqualTo("old-refresh-token");
        assertThat(response.accessExpiresInSeconds()).isEqualTo(900L);

        assertThat(refreshToken.getToken()).isEqualTo(response.refreshToken());
        assertThat(refreshToken.getExpiredAt()).isAfter(LocalDateTime.now());

        then(refreshTokenRepository).should().findByToken("old-refresh-token");
        then(tokenProvider).should().issueAccessToken(1L, Role.USER);
    }

    @Test
    @DisplayName("토큰 재발급 실패 - DB에 없는 refreshToken이면 InvalidRefreshTokenException이 발생한다")
    void refresh_fail_tokenNotFound() {
        // given
        RefreshRequest request = new RefreshRequest("invalid-refresh-token");

        given(refreshTokenRepository.findByToken("invalid-refresh-token"))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.refresh(request))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessage("유효하지 않은 Refresh Token입니다.");

        then(tokenProvider).should(never()).issueAccessToken(anyLong(), any(Role.class));
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 만료된 refreshToken이면 삭제 후 InvalidRefreshTokenException이 발생한다")
    void refresh_fail_expiredToken() {
        // given
        User user = createUser(
                1L,
                "test@test.com",
                "encoded-password",
                "테스터"
        );

        RefreshToken expiredRefreshToken = RefreshToken.create(
                user,
                "expired-refresh-token",
                LocalDateTime.now().minusSeconds(1)
        );

        RefreshRequest request = new RefreshRequest("expired-refresh-token");

        given(refreshTokenRepository.findByToken("expired-refresh-token"))
                .willReturn(Optional.of(expiredRefreshToken));

        // when & then
        assertThatThrownBy(() -> userService.refresh(request))
                .isInstanceOf(InvalidRefreshTokenException.class)
                .hasMessage("유효하지 않은 Refresh Token입니다.");

        then(refreshTokenRepository).should().delete(expiredRefreshToken);
        then(tokenProvider).should(never()).issueAccessToken(anyLong(), any(Role.class));
    }

    @Test
    @DisplayName("로그아웃 성공 - 현재 유저의 refreshToken을 모두 삭제한다")
    void logout_success() {
        // given
        Long userId = 1L;

        // when
        userService.logout(userId);

        // then
        then(refreshTokenRepository).should().deleteAllByUser_UserId(userId);
    }

    private User createUser(
            Long userId,
            String email,
            String encodedPassword,
            String nickname
    ) {
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .build();

        ReflectionTestUtils.setField(user, "userId", userId);

        return user;
    }
}