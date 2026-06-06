package io.yar.yar2026.user.controller;

import io.yar.yar2026.common.config.security.JwtAuthenticationFilter;
import io.yar.yar2026.common.config.security.TokenProvider;
import io.yar.yar2026.user.dto.LoginRequest;
import io.yar.yar2026.user.dto.RefreshRequest;
import io.yar.yar2026.user.dto.TokenResponse;
import io.yar.yar2026.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("로그인 성공 - 이메일과 비밀번호를 받아 토큰 응답을 반환한다")
    void login_success() throws Exception {
        // given
        LoginRequest request = new LoginRequest(
                "test@test.com",
                "password123"
        );

       TokenResponse response = new TokenResponse(
                "access-token",
                "refresh-token",
                900L
        );

        given(userService.login(request))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인되었습니다."))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.data.accessExpiresInSeconds").value(900));

        then(userService).should().login(request);
    }

    @Test
    @DisplayName("로그인 실패 - 이메일이 비어 있으면 400을 반환한다")
    void login_fail_blankEmail() throws Exception {
        // given
        String requestBody = """
                {
                  "email": "",
                  "password": "password123"
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이메일은 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("토큰 재발급 성공 - refreshToken을 받아 새 토큰 응답을 반환한다")
    void refresh_success() throws Exception {
        // given
        RefreshRequest request = new RefreshRequest("old-refresh-token");

        TokenResponse response = new TokenResponse(
                "new-access-token",
                "new-refresh-token",
                900L
        );

        given(userService.refresh(request))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").isEmpty())
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"))
                .andExpect(jsonPath("$.data.accessExpiresInSeconds").value(900));

        then(userService).should().refresh(request);
    }

    @Test
    @DisplayName("토큰 재발급 실패 - refreshToken이 비어 있으면 400을 반환한다")
    void refresh_fail_blankRefreshToken() throws Exception {
        // given
        String requestBody = """
                {
                  "refreshToken": ""
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Refresh Token은 필수입니다."))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("로그아웃 성공 - 인증된 userId의 refreshToken을 삭제한다")
    void logout_success() throws Exception {
        // given
        Principal principal = new UsernamePasswordAuthenticationToken(
                1L,
                null,
                List.of()
        );

        // when & then
        mockMvc.perform(post("/api/v1/auth/logout")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그아웃되었습니다."))
                .andExpect(jsonPath("$.data").isEmpty());

        then(userService).should().logout(1L);
    }
}