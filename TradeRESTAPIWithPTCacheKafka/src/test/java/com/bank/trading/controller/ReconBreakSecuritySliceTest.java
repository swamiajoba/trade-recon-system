package com.bank.trading.controller;


import com.bank.trading.config.SecurityConfig;
import com.bank.trading.security.JwtAuthenticationFilter;
import com.bank.trading.security.JwtUtil;
import com.bank.trading.service.ReconBreakService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReconBreakController.class)
@Import({
        SecurityConfig.class,
        JwtUtil.class,
        JwtAuthenticationFilter.class
})
@ActiveProfiles("test")
class ReconBreakSecuritySliceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private ReconBreakService reconBreakService;

    private String opsToken;

    @BeforeEach
    void setup() {

        opsToken =
                jwtUtil.generateToken(
                        "ops",
                        List.of("ROLE_OPS"));

        when(reconBreakService.getBreaks(any()))
                .thenReturn(Collections.emptyList());
    }

    @Test
    void givenValidOpsToken_whenGetBreaks_then200()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/recon/breaks")
                                .param("status", "OPEN")
                                .header(
                                        "Authorization",
                                        "Bearer " + opsToken))
                .andExpect(status().isOk());
    }

    @Test
    void givenNoToken_whenGetBreaks_then401()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/recon/breaks")
                                .param("status", "OPEN"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenInvalidToken_whenGetBreaks_then401()
            throws Exception {

        mockMvc.perform(
                        get("/api/v1/recon/breaks")
                                .param("status", "OPEN")
                                .header(
                                        "Authorization",
                                        "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenOpsToken_whenPutResolve_then403()
            throws Exception {

        mockMvc.perform(
                        put("/api/v1/recon/breaks/1/resolve")
                                .header(
                                        "Authorization",
                                        "Bearer " + opsToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAdminToken_whenPutResolve_then200()
            throws Exception {

        String token =
                jwtUtil.generateToken(
                        "admin",
                        List.of("ROLE_ADMIN"));

        mockMvc.perform(
                        put("/api/v1/recon/breaks/1/resolve")
                                .header(
                                        "Authorization",
                                        "Bearer " + token))
                .andExpect(status().isOk());
    }
}

//
//import com.bank.trading.config.SecurityConfig;
//import com.bank.trading.service.ReconBreakService;
//
//import org.junit.jupiter.api.Test;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
////import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
////
////import org.springframework.boot.test.mock.mockito.MockBean;
//
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.context.annotation.Import;
//
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ReconBreakController.class)
//@Import(SecurityConfig.class)
//
//class ReconBreakSecuritySliceTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
////    @MockBean
//    @MockitoBean
//    private ReconBreakService reconBreakService;
//
//    @Test
//    void givenOpsUser_whenGetBreaks_then200()
//            throws Exception {
//
//        mockMvc.perform(
//                        get("/api/v1/recon/breaks")
//                                .param("status", "OPEN")
//                                .with(
//                                        httpBasic(
//                                                "ops",
//                                                "ops123")))
//                .andExpect(
//                        status().isOk());
//    }
//
//    @Test
//    void givenNoCredentials_whenGetBreaks_then401()
//            throws Exception {
//
//        mockMvc.perform(
//                        get("/api/v1/recon/breaks")
//                                .param("status", "OPEN"))
//                .andExpect(
//                        status().isUnauthorized());
//    }
//}