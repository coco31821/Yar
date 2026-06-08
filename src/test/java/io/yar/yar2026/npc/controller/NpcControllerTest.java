package io.yar.yar2026.npc.controller;

import io.yar.yar2026.item.domain.ItemGrade;
import io.yar.yar2026.item.domain.ItemType;
import io.yar.yar2026.common.config.security.JwtAuthenticationFilter;
import io.yar.yar2026.common.config.security.TokenProvider;
import io.yar.yar2026.npc.NpcService;
import io.yar.yar2026.npc.dto.NpcResponse;
import io.yar.yar2026.npc.dto.NpcShopItemResponse;
import io.yar.yar2026.npc.exception.NpcNotFoundException;
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

@WebMvcTest(NpcController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("NpcController")
class NpcControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NpcService npcService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Nested
    @DisplayName("NPC 목록 조회")
    class GetNpcs {

        @Test
        @DisplayName("성공 시 NPC 목록을 반환한다")
        void getNpcs_success() throws Exception {
            // given
            List<NpcResponse> response = List.of(
                    new NpcResponse(
                            1L,
                            "npc_001",
                            "상인",
                            "마을의 잡화 상인입니다.",
                            "VILLAGE",
                            true,
                            List.of(new NpcShopItemResponse(
                                    10L,
                                    100L,
                                    "item_potion",
                                    "체력 물약",
                                    ItemType.CONSUMABLE,
                                    ItemGrade.COMMON,
                                    "체력을 회복합니다.",
                                    80,
                                    50,
                                    20,
                                    1
                            ))
                    ),
                    new NpcResponse(
                            2L,
                            "npc_002",
                            "대장장이",
                            "무기를 강화해주는 대장장이입니다.",
                            "FORGE",
                            true,
                            List.of()
                    )
            );

            given(npcService.getNpcs())
                    .willReturn(response);

            // when
            mockMvc.perform(get("/api/v1/npcs"))
            // then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").isEmpty())
                    .andExpect(jsonPath("$.data[0].npcId").value(1L))
                    .andExpect(jsonPath("$.data[0].rId").value("npc_001"))
                    .andExpect(jsonPath("$.data[0].name").value("상인"))
                    .andExpect(jsonPath("$.data[0].description").value("마을의 잡화 상인입니다."))
                    .andExpect(jsonPath("$.data[0].locationKey").value("VILLAGE"))
                    .andExpect(jsonPath("$.data[0].active").value(true))
                    .andExpect(jsonPath("$.data[0].shopItems[0].npcItemId").value(10L))
                    .andExpect(jsonPath("$.data[0].shopItems[0].itemId").value(100L))
                    .andExpect(jsonPath("$.data[0].shopItems[0].rId").value("item_potion"))
                    .andExpect(jsonPath("$.data[0].shopItems[0].itemName").value("체력 물약"))
                    .andExpect(jsonPath("$.data[0].shopItems[0].itemType").value("CONSUMABLE"))
                    .andExpect(jsonPath("$.data[0].shopItems[0].itemGrade").value("COMMON"))
                    .andExpect(jsonPath("$.data[0].shopItems[0].price").value(80))
                    .andExpect(jsonPath("$.data[0].shopItems[0].sellPrice").value(50))
                    .andExpect(jsonPath("$.data[0].shopItems[0].quantity").value(20))
                    .andExpect(jsonPath("$.data[0].shopItems[0].sortOrder").value(1))
                    .andExpect(jsonPath("$.data[1].npcId").value(2L))
                    .andExpect(jsonPath("$.data[1].rId").value("npc_002"))
                    .andExpect(jsonPath("$.data[1].shopItems").isEmpty());

            then(npcService).should().getNpcs();
        }
    }

    @Nested
    @DisplayName("NPC 단건 조회")
    class GetNpc {

        @Test
        @DisplayName("성공 시 NPC 단건을 반환한다")
        void getNpc_success() throws Exception {
            // given
            NpcResponse response = new NpcResponse(
                    1L,
                    "npc_001",
                    "상인",
                    "마을의 잡화 상인입니다.",
                    "VILLAGE",
                    true,
                    List.of(new NpcShopItemResponse(
                            10L,
                            100L,
                            "item_potion",
                            "체력 물약",
                            ItemType.CONSUMABLE,
                            ItemGrade.COMMON,
                            "체력을 회복합니다.",
                            80,
                            50,
                            20,
                            1
                    ))
            );

            given(npcService.getNpc(1L))
                    .willReturn(response);

            // when
            mockMvc.perform(get("/api/v1/npcs/1"))
            // then
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.message").isEmpty())
                    .andExpect(jsonPath("$.data.npcId").value(1L))
                    .andExpect(jsonPath("$.data.rId").value("npc_001"))
                    .andExpect(jsonPath("$.data.name").value("상인"))
                    .andExpect(jsonPath("$.data.description").value("마을의 잡화 상인입니다."))
                    .andExpect(jsonPath("$.data.locationKey").value("VILLAGE"))
                    .andExpect(jsonPath("$.data.active").value(true))
                    .andExpect(jsonPath("$.data.shopItems[0].npcItemId").value(10L))
                    .andExpect(jsonPath("$.data.shopItems[0].itemId").value(100L))
                    .andExpect(jsonPath("$.data.shopItems[0].rId").value("item_potion"))
                    .andExpect(jsonPath("$.data.shopItems[0].itemName").value("체력 물약"))
                    .andExpect(jsonPath("$.data.shopItems[0].itemType").value("CONSUMABLE"))
                    .andExpect(jsonPath("$.data.shopItems[0].itemGrade").value("COMMON"))
                    .andExpect(jsonPath("$.data.shopItems[0].price").value(80))
                    .andExpect(jsonPath("$.data.shopItems[0].sellPrice").value(50))
                    .andExpect(jsonPath("$.data.shopItems[0].quantity").value(20))
                    .andExpect(jsonPath("$.data.shopItems[0].sortOrder").value(1));

            then(npcService).should().getNpc(1L);
        }

        @Test
        @DisplayName("존재하지 않는 NPC이면 404 응답을 반환한다")
        void getNpc_notFound() throws Exception {
            // given
            given(npcService.getNpc(999L))
                    .willThrow(new NpcNotFoundException());

            // when
            mockMvc.perform(get("/api/v1/npcs/999"))
            // then
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").value("NPC를 찾을 수 없습니다."))
                    .andExpect(jsonPath("$.data").isEmpty());

            then(npcService).should().getNpc(999L);
        }
    }
}
