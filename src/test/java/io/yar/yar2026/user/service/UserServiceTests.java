package io.yar.yar2026.user.service;
import io.yar.yar2026.user.constants.Role;
import io.yar.yar2026.user.domain.User;
import io.yar.yar2026.user.dto.SignupRequest;
import io.yar.yar2026.user.dto.UserCreateResponse;
import io.yar.yar2026.user.exception.DuplicateEmailException;
import io.yar.yar2026.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("회원가입")
    class Signup {

        @Test
        @DisplayName("회원가입 성공 시 비밀번호를 암호화하고 유저를 저장한다")
        void signup_success() throws Exception {
            // given
            SignupRequest request = signupRequestOf();

            given(userRepository.existsByEmail(request.email()))
                    .willReturn(false);

            given(passwordEncoder.encode(request.password()))
                    .willReturn("encodedPassword");

            given(userRepository.save(any(User.class)))
                    .willAnswer(invocation -> {
                        User user = invocation.getArgument(0);
                        setField(user, "userId", 1L);
                        return user;
                    });

            // when
            UserCreateResponse response = userService.signup(request);

            // then
            assertThat(response.userId()).isEqualTo(1L);
            assertThat(response.email()).isEqualTo("test@test.com");
            assertThat(response.nickname()).isEqualTo("테스트유저");
            assertThat(response.role()).isEqualTo(Role.USER);
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.provider()).isEqualTo("LOCAL");
            assertThat(response.profileImageUrl()).isNull();
            assertThat(response.lastLoginAt()).isNull();

            then(userRepository)
                    .should()
                    .existsByEmail(request.email());

            then(passwordEncoder)
                    .should()
                    .encode(request.password());

            then(userRepository)
                    .should()
                    .save(any(User.class));
        }

        @Test
        @DisplayName("이미 사용 중인 이메일이면 DuplicateEmailException이 발생한다")
        void signup_fail_when_email_already_exists() {
            // given
            SignupRequest request = signupRequestOf();

            given(userRepository.existsByEmail(request.email()))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.signup(request))
                    .isInstanceOf(DuplicateEmailException.class)
                    .hasMessage("이미 사용 중인 이메일입니다.");

            then(userRepository)
                    .should()
                    .existsByEmail(request.email());

            then(passwordEncoder)
                    .shouldHaveNoInteractions();

            then(userRepository)
                    .should(never())
                    .save(any(User.class));
        }
    }

    private SignupRequest signupRequestOf() {
        return new SignupRequest(
                "test@test.com",
                "password123",
                "테스트유저"
        );
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}