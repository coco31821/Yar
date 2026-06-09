package io.yar.yar2026.npc.controller;

import io.yar.yar2026.common.config.security.JwtAuthenticationFilter;
import io.yar.yar2026.common.config.security.TokenProvider;
import io.yar.yar2026.inventory.dto.UserItemResponse;
import io.yar.yar2026.npc.NpcService;
import io.yar.yar2026.npc.dto.NpcPurchaseRequest;
import io.yar.yar2026.npc.dto.NpcPurchaseResponse;
import io.yar.yar2026.npc.exception.ShopItemNotFoundException;
import io.yar.yar2026.wallet.dto.WalletResponse;
import io.yar.yar2026.wallet.exception.NotEnoughGoldException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.security.Principal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NpcPurchaseController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("NpcPurchaseController")
class NpcPurchaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NpcService npcService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Nested
    @DisplayName("NPC 판매 아이템 구매")
    class Purchase {

        @Test
        @DisplayName("성공 시 구매 결과를 반환한다")
        void purchase_success() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );
            NpcPurchaseRequest request = new NpcPurchaseRequest(2);
            NpcPurchaseResponse response = new NpcPurchaseResponse(
                    new WalletResponse(400L, 10L),
                    new UserItemResponse(
                            200L,
                            10L,
                            "potion_hp_001",
                            "체력 물약",
                            "CONSUMABLE",
                            "COMMON",
                            "체력을 회복합니다.",
                            50,
                            15,
                            7,
                            false,
                            0,
                            "2026-06-08T11:00:00"
                    )
            );

            given(npcService.purchase(eq(1L), eq(1L), eq(100L), any(NpcPurchaseRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/users/me/npcs/{npcId}/items/{npcItemId}/purchase", 1L, 100L)
                            .principal(principal)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("구매가 완료되었습니다."))
                    .andExpect(jsonPath("$.data.wallet.gold").value(400L))
                    .andExpect(jsonPath("$.data.wallet.gem").value(10L))
                    .andExpect(jsonPath("$.data.acquiredItem.userItemId").value(200L))
                    .andExpect(jsonPath("$.data.acquiredItem.itemId").value(10L))
                    .andExpect(jsonPath("$.data.acquiredItem.itemName").value("체력 물약"))
                    .andExpect(jsonPath("$.data.acquiredItem.quantity").value(7));

            then(npcService).should().purchase(eq(1L), eq(1L), eq(100L), any(NpcPurchaseRequest.class));
        }

        @Test
        @DisplayName("요청 바디가 없으면 1개 구매로 처리한다")
        void purchase_success_when_body_is_empty() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );
            NpcPurchaseResponse response = new NpcPurchaseResponse(
                    new WalletResponse(450L, 10L),
                    null
            );

            given(npcService.purchase(1L, 1L, 100L, null))
                    .willReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/users/me/npcs/{npcId}/items/{npcItemId}/purchase", 1L, 100L)
                            .principal(principal))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.wallet.gold").value(450L));

            then(npcService).should().purchase(1L, 1L, 100L, null);
        }

        @Test
        @DisplayName("quantity가 1보다 작으면 400 응답을 반환한다")
        void purchase_fail_when_quantity_is_less_than_one() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );
            NpcPurchaseRequest request = new NpcPurchaseRequest(0);

            // when & then
            mockMvc.perform(post("/api/v1/users/me/npcs/{npcId}/items/{npcItemId}/purchase", 1L, 100L)
                            .principal(principal)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("수량은 1 이상이어야 합니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(npcService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("상점 아이템이 없으면 404 응답을 반환한다")
        void purchase_fail_when_shop_item_not_found() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            given(npcService.purchase(1L, 1L, 999L, null))
                    .willThrow(new ShopItemNotFoundException());

            // when & then
            mockMvc.perform(post("/api/v1/users/me/npcs/{npcId}/items/{npcItemId}/purchase", 1L, 999L)
                            .principal(principal))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("상점 아이템을 찾을 수 없습니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(npcService).should().purchase(1L, 1L, 999L, null);
        }

        @Test
        @DisplayName("골드가 부족하면 400 응답을 반환한다")
        void purchase_fail_when_gold_is_not_enough() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            given(npcService.purchase(1L, 1L, 100L, null))
                    .willThrow(new NotEnoughGoldException());

            // when & then
            mockMvc.perform(post("/api/v1/users/me/npcs/{npcId}/items/{npcItemId}/purchase", 1L, 100L)
                            .principal(principal))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("골드가 부족합니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(npcService).should().purchase(1L, 1L, 100L, null);
        }
    }
}
