package com.bank.trading.controller;

import com.bank.trading.config.SecurityConfig;
import com.bank.trading.domain.ReconBreak;
import com.bank.trading.security.JwtAuthenticationFilter;
import com.bank.trading.security.JwtUtil;
import com.bank.trading.service.ReconBreakService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;

import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReconBreakController.class)
@Import({
        SecurityConfig.class,
        JwtUtil.class,
        JwtAuthenticationFilter.class
})
@ActiveProfiles("test")
class ReconBreakSecurityPostPutSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private ReconBreakService reconBreakService;

    private String opsToken;
    private String adminToken;

    @BeforeEach
    void setup() {

        opsToken =
                jwtUtil.generateToken(
                        "ops",
                        List.of("ROLE_OPS"));

        adminToken =
                jwtUtil.generateToken(
                        "admin",
                        List.of("ROLE_ADMIN"));
    }

    @Test
    void givenOpsUser_whenCreateBreak_then201()
            throws Exception {

        ReconBreak savedBreak = new ReconBreak();

        savedBreak.setBreakId(5);
        savedBreak.setTradeId(22L);
        savedBreak.setBreakType(
                "QUANTITY_MISMATCH");

        savedBreak.setOurQty(1000);
        savedBreak.setTheirQty(900);

        savedBreak.setOurAmount(100000.00);
        savedBreak.setTheirAmount(90000.00);

        savedBreak.setStatus("OPEN");

        Mockito.when(
                        reconBreakService.createBreak(
                                Mockito.any(
                                        ReconBreak.class)))
                .thenReturn(savedBreak);

        String request = """
                {
                  "tradeId":22,
                  "breakType":"QUANTITY_MISMATCH",
                  "ourQty":1000,
                  "theirQty":900,
                  "ourAmount":100000.00,
                  "theirAmount":90000.00,
                  "status":"OPEN"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/recon/breaks")
                                .header(
                                        "Authorization",
                                        "Bearer " + opsToken)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(
                        status().isCreated())
                .andExpect(
                        header().string(
                                "Location",
                                "http://localhost/api/v1/recon/breaks/5"));
    }

    @Test
    void givenNoCredentials_whenCreateBreak_then401()
            throws Exception {

        String request = """
                {
                  "tradeId":23,
                  "breakType":"QUANTITY_MISMATCH",
                  "ourQty":1000,
                  "theirQty":900,
                  "ourAmount":100000.00,
                  "theirAmount":90000.00,
                  "status":"OPEN"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/recon/breaks")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(
                        status().isUnauthorized());
    }

    @Test
    void givenInvalidToken_whenCreateBreak_then401()
            throws Exception {

        String request = """
                {
                  "tradeId":23,
                  "breakType":"QUANTITY_MISMATCH",
                  "ourQty":1000,
                  "theirQty":900,
                  "ourAmount":100000.00,
                  "theirAmount":90000.00,
                  "status":"OPEN"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/recon/breaks")
                                .header(
                                        "Authorization",
                                        "Bearer invalid-token")
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(
                        status().isUnauthorized());
    }

    @Test
    void givenOpsUser_whenResolveBreak_then403()
            throws Exception {

        mockMvc.perform(
                        put(
                                "/api/v1/recon/breaks/1/resolve")
                                .header(
                                        "Authorization",
                                        "Bearer " + opsToken))
                .andExpect(
                        status().isForbidden());
    }

    @Test
    void givenAdminUser_whenResolveBreak_then200()
            throws Exception {
        ReconBreak resolved = new ReconBreak();
        resolved.setBreakId(1);
        resolved.setStatus("RESOLVED");

        Mockito.when(
                        reconBreakService.resolveBreak(1))
                .thenReturn(resolved);

        mockMvc.perform(
                        put(
                                "/api/v1/recon/breaks/1/resolve")
                                .header(
                                        "Authorization",
                                        "Bearer " + adminToken))
                .andExpect(
                        status().isOk());
    }
}



//package com.bank.trading.controller;
//
//import com.bank.trading.config.SecurityConfig;
//import com.bank.trading.domain.ReconBreak;
//import com.bank.trading.service.ReconBreakService;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.context.annotation.Import;
//
//import org.springframework.http.MediaType;
//
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ReconBreakController.class)
//@Import(SecurityConfig.class)
//
//class ReconBreakSecurityPostPutSliceTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private ReconBreakService reconBreakService;
//
//    @Test
//    void givenOpsUser_whenCreateBreak_then201()
//            throws Exception {
//
//        ReconBreak savedBreak = new ReconBreak();
//
//        savedBreak.setBreakId(5);
//        savedBreak.setTradeId(22L);
//        savedBreak.setBreakType("QUANTITY_MISMATCH");
//
//        savedBreak.setOurQty(1000);
//        savedBreak.setTheirQty(900);
//
//        savedBreak.setOurAmount(100000.00);
//        savedBreak.setTheirAmount(90000.00);
//
//        savedBreak.setStatus("OPEN");
//
//        Mockito.when(
//                        reconBreakService.createBreak(
//                                Mockito.any(ReconBreak.class)))
//                .thenReturn(savedBreak);
//
//        String request = """
//                {
//                  "tradeId":22,
//                  "breakType":"QUANTITY_MISMATCH",
//                  "ourQty":1000,
//                  "theirQty":900,
//                  "ourAmount":100000.00,
//                  "theirAmount":90000.00,
//                  "status":"OPEN"
//                }
//                """;
//
//        mockMvc.perform(
//                        post("/api/v1/recon/breaks")
//                                .with(
//                                        httpBasic(
//                                                "ops",
//                                                "ops123"))
//                                .contentType(
//                                        MediaType.APPLICATION_JSON)
//                                .content(request))
//                .andExpect(
//                        status().isCreated())
//                .andExpect(
//                        header().string(
//                                "Location",
//                                "http://localhost/api/v1/recon/breaks/5"));
//    }
//
//
//
//    @Test
//    void givenNoCredentials_whenCreateBreak_then401()
//            throws Exception {
//
//        String request = """
//                {
//                  "tradeId":23,
//                  "breakType":"QUANTITY_MISMATCH",
//                  "ourQty":1000,
//                  "theirQty":900,
//                  "ourAmount":100000.00,
//                  "theirAmount":90000.00,
//                  "status":"OPEN"
//                }
//                """;
//
//        mockMvc.perform(
//                        post("/api/v1/recon/breaks")
//                                .contentType(
//                                        MediaType.APPLICATION_JSON)
//                                .content(request))
//                .andExpect(
//                        status().isUnauthorized());
//    }
//
//    @Test
//    void givenOpsUser_whenResolveBreak_then403()
//            throws Exception {
//
//        mockMvc.perform(
//                        put(
//                                "/api/v1/recon/breaks/1/resolve")
//                                .with(
//                                        httpBasic(
//                                                "ops",
//                                                "ops123")))
//                .andExpect(
//                        status().isForbidden());
//    }
//}