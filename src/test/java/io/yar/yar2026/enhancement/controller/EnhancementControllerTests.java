package io.yar.yar2026.enhancement.controller;

import io.yar.yar2026.common.config.security.JwtAuthenticationFilter;
import io.yar.yar2026.common.config.security.TokenProvider;
import io.yar.yar2026.enhancement.dto.EnhancementInfoResponse;
import io.yar.yar2026.enhancement.dto.EnhancementResultResponse;
import io.yar.yar2026.enhancement.exception.ItemNotEnhanceableException;
import io.yar.yar2026.enhancement.service.EnhancementService;
import io.yar.yar2026.inventory.dto.UserItemResponse;
import io.yar.yar2026.inventory.exception.UserItemNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnhancementController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("EnhancementController")
class EnhancementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnhancementService enhancementService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Nested
    @DisplayName("강화창 정보 조회")
    class GetEnhancementInfo {

        @Test
        @DisplayName("성공 시 강화 정보를 반환한다")
        void getEnhancementInfo_success() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            EnhancementInfoResponse response = new EnhancementInfoResponse(
                    10L,
                    0,
                    5,
                    false,
                    70,
                    30,
                    0,
                    100,
                    true
            );

            given(enhancementService.getEnhancementInfo(1L, 10L))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/users/me/inventory/{userItemId}/enhance", 10L)
                            .principal(principal))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("강화 정보를 조회했습니다."))
                    .andExpect(jsonPath("$.data.userItemId").value(10L))
                    .andExpect(jsonPath("$.data.currentGrade").value(0))
                    .andExpect(jsonPath("$.data.maxGrade").value(5))
                    .andExpect(jsonPath("$.data.maxed").value(false))
                    .andExpect(jsonPath("$.data.successRate").value(70))
                    .andExpect(jsonPath("$.data.failRate").value(30))
                    .andExpect(jsonPath("$.data.destroyRate").value(0))
                    .andExpect(jsonPath("$.data.goldCost").value(100))
                    .andExpect(jsonPath("$.data.miracleTime").value(true));

            then(enhancementService).should().getEnhancementInfo(1L, 10L);
        }

        @Test
        @DisplayName("보유 아이템이 없으면 404 응답을 반환한다")
        void getEnhancementInfo_fail_when_user_item_not_found() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            given(enhancementService.getEnhancementInfo(1L, 999L))
                    .willThrow(new UserItemNotFoundException());

            // when & then
            mockMvc.perform(get("/api/v1/users/me/inventory/{userItemId}/enhance", 999L)
                            .principal(principal))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("아이템을 찾을 수 없습니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(enhancementService).should().getEnhancementInfo(1L, 999L);
        }

        @Test
        @DisplayName("강화할 수 없는 아이템이면 400 응답을 반환한다")
        void getEnhancementInfo_fail_when_item_is_not_enhanceable() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            given(enhancementService.getEnhancementInfo(1L, 10L))
                    .willThrow(new ItemNotEnhanceableException());

            // when & then
            mockMvc.perform(get("/api/v1/users/me/inventory/{userItemId}/enhance", 10L)
                            .principal(principal))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("강화할 수 없는 아이템입니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(enhancementService).should().getEnhancementInfo(1L, 10L);
        }
    }

    @Nested
    @DisplayName("강화 실행")
    class Enhance {

        @Test
        @DisplayName("성공 시 강화 결과를 반환한다")
        void enhance_success() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );
            UserItemResponse userItem = new UserItemResponse(
                    10L,
                    1L,
                    "wooden_bow",
                    "나무 활",
                    "WEAPON",
                    "COMMON",
                    "설명",
                    100,
                    50,
                    1,
                    false,
                    1,
                    "2026-06-07T10:00:00"
            );
            EnhancementResultResponse response = new EnhancementResultResponse(
                    "SUCCESS",
                    false,
                    0,
                    1,
                    10,
                    90,
                    userItem
            );

            given(enhancementService.enhance(1L, 10L))
                    .willReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/users/me/inventory/{userItemId}/enhance", 10L)
                            .principal(principal))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("강화가 완료되었습니다."))
                    .andExpect(jsonPath("$.data.outcome").value("SUCCESS"))
                    .andExpect(jsonPath("$.data.miracleTimeApplied").value(false))
                    .andExpect(jsonPath("$.data.gradeBefore").value(0))
                    .andExpect(jsonPath("$.data.gradeAfter").value(1))
                    .andExpect(jsonPath("$.data.goldSpent").value(10))
                    .andExpect(jsonPath("$.data.remainingGold").value(90))
                    .andExpect(jsonPath("$.data.userItem.userItemId").value(10L))
                    .andExpect(jsonPath("$.data.userItem.enhancementGrade").value(1));

            then(enhancementService).should().enhance(1L, 10L);
        }
    }
}
