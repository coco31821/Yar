package io.yar.yar2026.item.controller;

import io.yar.yar2026.common.config.security.JwtAuthenticationFilter;
import io.yar.yar2026.common.config.security.TokenProvider;
import io.yar.yar2026.item.ItemService;
import io.yar.yar2026.item.dto.ItemResponse;
import io.yar.yar2026.item.exception.ItemNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("ItemController")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Nested
    @DisplayName("전체 아이템 목록 조회")
    class GetItems {

        @Test
        @DisplayName("성공 시 아이템 목록을 반환한다")
        void getItems_success() throws Exception {
            // given
            List<ItemResponse> response = List.of(
                    new ItemResponse(
                            1L,
                            "sword_001",
                            "연습용 검",
                            "WEAPON",
                            "COMMON",
                            "초보자용 검입니다.",
                            100,
                            50
                    ),
                    new ItemResponse(
                            2L,
                            "potion_hp_001",
                            "HP 포션",
                            "CONSUMABLE",
                            "COMMON",
                            "HP를 50 회복합니다.",
                            30,
                            10
                    )
            );

            given(itemService.getItems())
                    .willReturn(response);

            // when
            mockMvc.perform(get("/api/v1/items"))
            // then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").isEmpty())
                    .andExpect(jsonPath("$.data[0].itemId").value(1L))
                    .andExpect(jsonPath("$.data[0].rId").value("sword_001"))
                    .andExpect(jsonPath("$.data[0].itemName").value("연습용 검"))
                    .andExpect(jsonPath("$.data[0].itemType").value("WEAPON"))
                    .andExpect(jsonPath("$.data[0].itemGrade").value("COMMON"))
                    .andExpect(jsonPath("$.data[0].description").value("초보자용 검입니다."))
                    .andExpect(jsonPath("$.data[0].price").value(100))
                    .andExpect(jsonPath("$.data[0].sellPrice").value(50))
                    .andExpect(jsonPath("$.data[1].itemId").value(2L))
                    .andExpect(jsonPath("$.data[1].rId").value("potion_hp_001"));

            then(itemService).should().getItems();
        }
    }

    @Nested
    @DisplayName("아이템 단건 조회")
    class GetItem {

        @Test
        @DisplayName("성공 시 아이템 단건을 반환한다")
        void getItem_success() throws Exception {
            // given
            ItemResponse response = new ItemResponse(
                    1L,
                    "sword_001",
                    "연습용 검",
                    "WEAPON",
                    "COMMON",
                    "초보자용 검입니다.",
                    100,
                    50
            );

            given(itemService.getItem(1L))
                    .willReturn(response);

            // when
            mockMvc.perform(get("/api/v1/items/1"))
            // then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").isEmpty())
                    .andExpect(jsonPath("$.data.itemId").value(1L))
                    .andExpect(jsonPath("$.data.rId").value("sword_001"))
                    .andExpect(jsonPath("$.data.itemName").value("연습용 검"))
                    .andExpect(jsonPath("$.data.itemType").value("WEAPON"))
                    .andExpect(jsonPath("$.data.itemGrade").value("COMMON"))
                    .andExpect(jsonPath("$.data.description").value("초보자용 검입니다."))
                    .andExpect(jsonPath("$.data.price").value(100))
                    .andExpect(jsonPath("$.data.sellPrice").value(50));

            then(itemService).should().getItem(1L);
        }

        @Test
        @DisplayName("존재하지 않는 아이템이면 404 응답을 반환한다")
        void getItem_fail_when_item_not_found() throws Exception {
            // given
            given(itemService.getItem(999L))
                    .willThrow(new ItemNotFoundException());

            // when
            mockMvc.perform(get("/api/v1/items/999"))
            // then
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("아이템을 찾을 수 없습니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(itemService).should().getItem(999L);
        }
    }
}
