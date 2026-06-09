package io.yar.yar2026.enhancement.controller;

import io.yar.yar2026.common.config.security.JwtAuthenticationFilter;
import io.yar.yar2026.common.config.security.TokenProvider;
import io.yar.yar2026.enhancement.dto.MiracleTimeResponse;
import io.yar.yar2026.enhancement.service.EnhancementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MiracleTimeController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MiracleTimeController")
class MiracleTimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnhancementService enhancementService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("성공 시 미라클 타임 정보를 반환한다")
    void getMiracleTime_success() throws Exception {
        // given
        MiracleTimeResponse response = new MiracleTimeResponse(
                true,
                "미라클 타임 진행 중! 강화 성공 시 등급이 2배로 상승합니다."
        );

        given(enhancementService.getMiracleTime())
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/enhancement/miracle-time"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("미라클 타임 정보를 조회했습니다."))
                .andExpect(jsonPath("$.data.miracleTime").value(true))
                .andExpect(jsonPath("$.data.message").value("미라클 타임 진행 중! 강화 성공 시 등급이 2배로 상승합니다."));

        then(enhancementService).should().getMiracleTime();
    }
}