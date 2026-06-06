package io.yar.yar2026.user.controller;

import io.yar.yar2026.common.config.security.JwtAuthenticationFilter;
import io.yar.yar2026.common.config.security.TokenProvider;
import io.yar.yar2026.user.constants.Role;
import io.yar.yar2026.user.dto.SignupRequest;
import io.yar.yar2026.user.dto.UserCreateResponse;
import io.yar.yar2026.user.exception.DuplicateEmailException;
import io.yar.yar2026.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;


    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Nested
    @DisplayName("회원가입")
    class Signup {

        @Test
        @DisplayName("회원가입 성공 시 성공 응답을 반환한다")
        void signup_success() throws Exception {
            // given
            SignupRequest request = signupRequestOf();

            UserCreateResponse response = new UserCreateResponse(
                    1L,
                    "test@test.com",
                    "테스트유저",
                    Role.USER,
                    "ACTIVE",
                    "LOCAL",
                    null,
                    null,
                    null
            );

            given(userService.signup(any(SignupRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(
                            post("/api/v1/users/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("가입되었습니다."))
                    .andExpect(jsonPath("$.data.userId").value(1L))
                    .andExpect(jsonPath("$.data.email").value("test@test.com"))
                    .andExpect(jsonPath("$.data.nickname").value("테스트유저"))
                    .andExpect(jsonPath("$.data.role").value("USER"))
                    .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                    .andExpect(jsonPath("$.data.provider").value("LOCAL"));

            then(userService)
                    .should()
                    .signup(any(SignupRequest.class));
        }

        @Test
        @DisplayName("이미 사용 중인 이메일이면 409 응답을 반환한다")
        void signup_fail_when_email_already_exists() throws Exception {
            // given
            SignupRequest request = signupRequestOf();

            given(userService.signup(any(SignupRequest.class)))
                    .willThrow(new DuplicateEmailException());

            // when & then
            mockMvc.perform(
                            post("/api/v1/users/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(userService)
                    .should()
                    .signup(any(SignupRequest.class));
        }

        @Test
        @DisplayName("비밀번호가 8자 미만이면 400 응답을 반환한다")
        void signup_fail_when_password_too_short() throws Exception {
            // given
            SignupRequest request = new SignupRequest(
                    "test@test.com",
                    "1234",
                    "테스트유저"
            );

            // when & then
            mockMvc.perform(
                            post("/api/v1/users/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("비밀번호는 8~64자여야 합니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(userService)
                    .shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("이메일 형식이 올바르지 않으면 400 응답을 반환한다")
        void signup_fail_when_email_invalid() throws Exception {
            // given
            SignupRequest request = new SignupRequest(
                    "invalid-email",
                    "password123",
                    "테스트유저"
            );

            // when & then
            mockMvc.perform(
                            post("/api/v1/users/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("이메일 형식이 올바르지 않습니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(userService)
                    .shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("닉네임이 2자 미만이면 400 응답을 반환한다")
        void signup_fail_when_nickname_too_short() throws Exception {
            // given
            SignupRequest request = new SignupRequest(
                    "test@test.com",
                    "password123",
                    "닉"
            );

            // when & then
            mockMvc.perform(
                            post("/api/v1/users/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("닉네임은 2~20자여야 합니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(userService)
                    .shouldHaveNoInteractions();
        }
    }

    private SignupRequest signupRequestOf() {
        return new SignupRequest(
                "test@test.com",
                "password123",
                "테스트유저"
        );
    }
}