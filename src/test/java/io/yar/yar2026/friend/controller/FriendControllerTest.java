package io.yar.yar2026.friend.controller;

import io.yar.yar2026.common.config.security.JwtAuthenticationFilter;
import io.yar.yar2026.common.config.security.TokenProvider;
import io.yar.yar2026.friend.FriendService;
import io.yar.yar2026.friend.domain.FriendRequestStatus;
import io.yar.yar2026.friend.dto.FriendRequestCreateRequest;
import io.yar.yar2026.friend.dto.FriendRequestResponse;
import io.yar.yar2026.friend.exception.DuplicateFriendRequestException;
import io.yar.yar2026.friend.exception.SelfFriendRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("FriendController")
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FriendService friendService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Nested
    @DisplayName("친구 요청 생성")
    class CreateFriendRequest {

        @Test
        @DisplayName("성공 시 친구 요청 생성 응답을 반환한다")
        void createFriendRequest_success() throws Exception {
            // given
            Long userId = 1L;
            FriendRequestCreateRequest request = new FriendRequestCreateRequest(2L);
            FriendRequestResponse response = new FriendRequestResponse(
                    10L,
                    userId,
                    2L,
                    FriendRequestStatus.PENDING,
                    LocalDateTime.of(2026, 6, 7, 10, 0),
                    "받는유저"
            );

            given(friendService.createFriendRequest(eq(userId), any(FriendRequestCreateRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(
                            post("/api/v1/users/me/friends/requests")
                                    .principal(new UsernamePasswordAuthenticationToken(userId, null))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("친구 요청을 보냈습니다."))
                    .andExpect(jsonPath("$.data.friendRequestId").value(10L))
                    .andExpect(jsonPath("$.data.fromUserId").value(userId))
                    .andExpect(jsonPath("$.data.toUserId").value(2L))
                    .andExpect(jsonPath("$.data.status").value("PENDING"))
                    .andExpect(jsonPath("$.data.nickname").value("받는유저"));

            then(friendService).should()
                    .createFriendRequest(eq(userId), any(FriendRequestCreateRequest.class));
        }

        @Test
        @DisplayName("자기 자신에게 요청하면 400 응답을 반환한다")
        void createFriendRequest_fail_when_self_request() throws Exception {
            // given
            Long userId = 1L;
            FriendRequestCreateRequest request = new FriendRequestCreateRequest(userId);

            given(friendService.createFriendRequest(eq(userId), any(FriendRequestCreateRequest.class)))
                    .willThrow(new SelfFriendRequestException());

            // when & then
            mockMvc.perform(
                            post("/api/v1/users/me/friends/requests")
                                    .principal(new UsernamePasswordAuthenticationToken(userId, null))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("자기 자신에게 친구 요청을 보낼 수 없습니다."))
                    .andExpect(jsonPath("$.data").isEmpty());
        }

        @Test
        @DisplayName("이미 요청 또는 관계가 있으면 409 응답을 반환한다")
        void createFriendRequest_fail_when_duplicate() throws Exception {
            // given
            Long userId = 1L;
            FriendRequestCreateRequest request = new FriendRequestCreateRequest(2L);

            given(friendService.createFriendRequest(eq(userId), any(FriendRequestCreateRequest.class)))
                    .willThrow(new DuplicateFriendRequestException());

            // when & then
            mockMvc.perform(
                            post("/api/v1/users/me/friends/requests")
                                    .principal(new UsernamePasswordAuthenticationToken(userId, null))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("이미 친구 관계입니다. / 이미 보낸 친구 요청이 있습니다."))
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }
}
