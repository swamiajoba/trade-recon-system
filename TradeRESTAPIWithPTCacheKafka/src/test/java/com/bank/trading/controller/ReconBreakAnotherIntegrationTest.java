package com.bank.trading.controller;

import com.bank.trading.domain.ReconBreak;
import com.bank.trading.repository.ReconBreakRepository;
import com.bank.trading.security.JwtUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.http.MediaType;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class ReconBreakAnotherIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReconBreakRepository repository;

    @Autowired
    private JwtUtil jwtUtil;

    private String opsToken;

    @BeforeEach
    void cleanUp() {

        repository.deleteAll();

        opsToken =
                jwtUtil.generateToken(
                        "ops",
                        List.of("ROLE_OPS"));
    }

    @Test
    void givenOpsUser_whenCreateBreak_thenSavedInDatabase()
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
                                        "Bearer " + opsToken)
                                .contentType(
                                        MediaType.APPLICATION_JSON)
                                .content(request))
                .andExpect(
                        status().isCreated());

        List<ReconBreak> breaks =
                repository.findByStatus("OPEN");

        assertFalse(breaks.isEmpty());

        assertEquals(
                "QUANTITY_MISMATCH",
                breaks.get(0).getBreakType());
    }


    @Test
    void givenNoToken_whenCreateBreak_then401()
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
}



//package com.bank.trading.controller;
//
//import com.bank.trading.domain.ReconBreak;
//import com.bank.trading.repository.ReconBreakRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.http.MediaType;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
//@ActiveProfiles("test") // uses application-test.yml with H2 config
//public class ReconBreakAnotherIntegrationTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ReconBreakRepository repository;
//
//    @BeforeEach
//    void cleanUp() {
//        repository.deleteAll();
//    }
//
//
//    @Test
//    void givenOpsUser_whenCreateBreak_thenSavedInDatabase()
//            throws Exception {
//
//        String request = """
//            {
//              "tradeId":23,
//              "breakType":"QUANTITY_MISMATCH",
//              "ourQty":1000,
//              "theirQty":900,
//              "ourAmount":100000.00,
//              "theirAmount":90000.00,
//              "status":"OPEN"
//            }
//            """;
//
//        mockMvc.perform(
//                        post("/api/v1/recon/breaks")
//                                .with(httpBasic("ops", "ops123"))
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(request))
//                .andExpect(status().isCreated());
//
//        List<ReconBreak> breaks =
//                repository.findByStatus("OPEN");
//
//        assertFalse(breaks.isEmpty());
//
//        assertEquals(
//                "QUANTITY_MISMATCH",
//                breaks.get(0).getBreakType());
//    }
//}
