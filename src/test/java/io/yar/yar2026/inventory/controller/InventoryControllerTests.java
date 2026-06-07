package io.yar.yar2026.inventory.controller;

import io.yar.yar2026.common.config.security.JwtAuthenticationFilter;
import io.yar.yar2026.common.config.security.TokenProvider;
import io.yar.yar2026.inventory.dto.ItemPickupRequest;
import io.yar.yar2026.inventory.dto.UserItemResponse;
import io.yar.yar2026.inventory.exception.NotEnoughItemQuantityException;
import io.yar.yar2026.inventory.exception.UserItemNotFoundException;
import io.yar.yar2026.inventory.service.InventoryService;
import io.yar.yar2026.item.exception.ItemNotFoundException;
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
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("InventoryController")
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventoryService inventoryService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Nested
    @DisplayName("인벤토리 조회")
    class GetInventory {

        @Test
        @DisplayName("성공 시 보유 아이템 목록을 반환한다")
        void getInventory_success() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            List<UserItemResponse> response = List.of(
                    new UserItemResponse(
                            10L,
                            1L,
                            "sword_001",
                            "연습용 검",
                            "WEAPON",
                            "COMMON",
                            "초보자용 검입니다.",
                            100,
                            50,
                            1,
                            false,
                            "2026-06-07T10:00:00"
                    ),
                    new UserItemResponse(
                            11L,
                            2L,
                            "potion_hp_001",
                            "HP 포션",
                            "CONSUMABLE",
                            "COMMON",
                            "HP를 50 회복합니다.",
                            30,
                            10,
                            5,
                            false,
                            "2026-06-07T10:05:00"
                    )
            );

            given(inventoryService.getInventory(1L))
                    .willReturn(response);

            // when & then
            mockMvc.perform(get("/api/v1/users/me/inventory")
                            .principal(principal))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").isEmpty())
                    .andExpect(jsonPath("$.data[0].userItemId").value(10L))
                    .andExpect(jsonPath("$.data[0].itemId").value(1L))
                    .andExpect(jsonPath("$.data[0].rId").value("sword_001"))
                    .andExpect(jsonPath("$.data[0].itemName").value("연습용 검"))
                    .andExpect(jsonPath("$.data[0].itemType").value("WEAPON"))
                    .andExpect(jsonPath("$.data[0].itemGrade").value("COMMON"))
                    .andExpect(jsonPath("$.data[0].description").value("초보자용 검입니다."))
                    .andExpect(jsonPath("$.data[0].price").value(100))
                    .andExpect(jsonPath("$.data[0].sellPrice").value(50))
                    .andExpect(jsonPath("$.data[0].quantity").value(1))
                    .andExpect(jsonPath("$.data[0].equipped").value(false))
                    .andExpect(jsonPath("$.data[0].acquiredAt").value("2026-06-07T10:00:00"))
                    .andExpect(jsonPath("$.data[1].userItemId").value(11L))
                    .andExpect(jsonPath("$.data[1].itemId").value(2L))
                    .andExpect(jsonPath("$.data[1].quantity").value(5));

            then(inventoryService).should().getInventory(1L);
        }
    }

    @Nested
    @DisplayName("아이템 획득")
    class Pickup {

        @Test
        @DisplayName("성공 시 획득한 아이템을 반환한다")
        void pickup_success() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            ItemPickupRequest request = new ItemPickupRequest(1L, 3);

            UserItemResponse response = new UserItemResponse(
                    10L,
                    1L,
                    "potion_hp_001",
                    "HP 포션",
                    "CONSUMABLE",
                    "COMMON",
                    "HP를 50 회복합니다.",
                    30,
                    10,
                    3,
                    false,
                    "2026-06-07T10:00:00"
            );

            given(inventoryService.pickup(eq(1L), any(ItemPickupRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(post("/api/v1/users/me/inventory/pickup")
                            .principal(principal)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("아이템을 획득했습니다."))
                    .andExpect(jsonPath("$.data.userItemId").value(10L))
                    .andExpect(jsonPath("$.data.itemId").value(1L))
                    .andExpect(jsonPath("$.data.rId").value("potion_hp_001"))
                    .andExpect(jsonPath("$.data.itemName").value("HP 포션"))
                    .andExpect(jsonPath("$.data.itemType").value("CONSUMABLE"))
                    .andExpect(jsonPath("$.data.itemGrade").value("COMMON"))
                    .andExpect(jsonPath("$.data.description").value("HP를 50 회복합니다."))
                    .andExpect(jsonPath("$.data.price").value(30))
                    .andExpect(jsonPath("$.data.sellPrice").value(10))
                    .andExpect(jsonPath("$.data.quantity").value(3))
                    .andExpect(jsonPath("$.data.equipped").value(false))
                    .andExpect(jsonPath("$.data.acquiredAt").value("2026-06-07T10:00:00"));

            then(inventoryService).should().pickup(eq(1L), any(ItemPickupRequest.class));
        }

        @Test
        @DisplayName("itemId가 없으면 400 응답을 반환한다")
        void pickup_fail_when_item_id_is_null() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            String requestBody = """
                    {
                      "quantity": 1
                    }
                    """;

            // when & then
            mockMvc.perform(post("/api/v1/users/me/inventory/pickup")
                            .principal(principal)
                            .contentType(APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("아이템 ID는 필수입니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(inventoryService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("quantity가 1보다 작으면 400 응답을 반환한다")
        void pickup_fail_when_quantity_is_less_than_one() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            ItemPickupRequest request = new ItemPickupRequest(1L, 0);

            // when & then
            mockMvc.perform(post("/api/v1/users/me/inventory/pickup")
                            .principal(principal)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("수량은 1 이상이어야 합니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(inventoryService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("존재하지 않는 아이템이면 404 응답을 반환한다")
        void pickup_fail_when_item_not_found() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            ItemPickupRequest request = new ItemPickupRequest(999L, 1);

            given(inventoryService.pickup(eq(1L), any(ItemPickupRequest.class)))
                    .willThrow(new ItemNotFoundException());

            // when & then
            mockMvc.perform(post("/api/v1/users/me/inventory/pickup")
                            .principal(principal)
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("아이템을 찾을 수 없습니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(inventoryService).should().pickup(eq(1L), any(ItemPickupRequest.class));
        }
    }

    @Nested
    @DisplayName("아이템 버리기")
    class Discard {

        @Test
        @DisplayName("성공 시 아이템을 버리고 성공 응답을 반환한다")
        void discard_success() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            // when & then
            mockMvc.perform(delete("/api/v1/users/me/inventory/{userItemId}/discard", 10L)
                            .principal(principal)
                            .queryParam("quantity", "2"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").value("아이템을 버렸습니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(inventoryService).should().discard(1L, 10L, 2);
        }

        @Test
        @DisplayName("quantity가 1보다 작으면 400 응답을 반환한다")
        void discard_fail_when_quantity_is_less_than_one() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            // when & then
            mockMvc.perform(delete("/api/v1/users/me/inventory/{userItemId}/discard", 10L)
                            .principal(principal)
                            .queryParam("quantity", "0"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("수량은 1 이상이어야 합니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(inventoryService).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("보유 아이템이 없으면 404 응답을 반환한다")
        void discard_fail_when_user_item_not_found() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            willThrow(new UserItemNotFoundException())
                    .given(inventoryService)
                    .discard(1L, 999L, 1);

            // when & then
            mockMvc.perform(delete("/api/v1/users/me/inventory/{userItemId}/discard", 999L)
                            .principal(principal)
                            .queryParam("quantity", "1"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("아이템을 찾을 수 없습니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(inventoryService).should().discard(1L, 999L, 1);
        }

        @Test
        @DisplayName("아이템 수량이 부족하면 400 응답을 반환한다")
        void discard_fail_when_quantity_is_not_enough() throws Exception {
            // given
            Principal principal = new UsernamePasswordAuthenticationToken(
                    1L,
                    null,
                    List.of()
            );

            willThrow(new NotEnoughItemQuantityException())
                    .given(inventoryService)
                    .discard(1L, 10L, 3);

            // when & then
            mockMvc.perform(delete("/api/v1/users/me/inventory/{userItemId}/discard", 10L)
                            .principal(principal)
                            .queryParam("quantity", "3"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("아이템 수량이 부족합니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(inventoryService).should().discard(1L, 10L, 3);
        }
    }
}
