//package com.bank.trading.controller;
//
//
//import com.bank.trading.domain.ReconBreak;
//import com.bank.trading.repository.ReconBreakRepository;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
//import org.springframework.boot.resttestclient.TestRestTemplate;
//import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
//import org.springframework.boot.test.context.SpringBootTest;
////import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import org.springframework.test.context.ActiveProfiles;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureTestRestTemplate  // REQUIRED in Boot 4
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
//@ActiveProfiles("test") // uses application-test.yml with H2 config
//class ReconBreakIntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    // TestRestTemplate is pre-configured for basic auth per instance
//    @Autowired
//    private TestRestTemplate restTemplate;
//
//
//
//    @Autowired
//    private ReconBreakRepository reconBreakRepository;  // real repo
//
//    @BeforeEach
//    void cleanUp() {
//        reconBreakRepository.deleteAll();
//    }
//
//    // ----------------------------------------------------------------
//    // GET /api/v1/recon/breaks?status=OPEN
//    // ----------------------------------------------------------------
//
//    @Test
//    void givenOpsUser_whenGetBreaks_then200() {
//        // Seed real data into the embedded DB
//        ReconBreak seeded = new ReconBreak();
//        seeded.setTradeId(10L);
//        seeded.setBreakType("QUANTITY_MISMATCH");
//        seeded.setOurQty(500);
//        seeded.setTheirQty(400);
//        seeded.setOurAmount(50000.00);
//        seeded.setTheirAmount(40000.00);
//        seeded.setStatus("OPEN");
//        reconBreakRepository.save(seeded);
//
//        ResponseEntity<String> response = restTemplate
//                .withBasicAuth("ops", "ops123")
//                .getForEntity(
//                        "http://localhost:" + port + "/api/v1/recon/breaks?status=OPEN",
//                        String.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).contains("QUANTITY_MISMATCH");
//    }
//
//    @Test
//    void givenNoCredentials_whenGetBreaks_then401() {
//        ResponseEntity<String> response = restTemplate
//                .getForEntity(
//                        "http://localhost:" + port + "/api/v1/recon/breaks?status=OPEN",
//                        String.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
//    }
//
//    // ----------------------------------------------------------------
//    // POST /api/v1/recon/breaks
//    // ----------------------------------------------------------------
//
//    @Test
//    void givenOpsUser_whenCreateBreak_then201() {
//        ReconBreak request = new ReconBreak();
//        request.setTradeId(22L);
//        request.setBreakType("QUANTITY_MISMATCH");
//        request.setOurQty(1000);
//        request.setTheirQty(900);
//        request.setOurAmount(100000.00);
//        request.setTheirAmount(90000.00);
//        request.setStatus("OPEN");
//
//        ResponseEntity<ReconBreak> response = restTemplate
//                .withBasicAuth("ops", "ops123")
//                .postForEntity(
//                        "http://localhost:" + port + "/api/v1/recon/breaks",
//                        request,
//                        ReconBreak.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//
//        // Location header points to the newly created resource
//        assertThat(response.getHeaders().getLocation())
//                .hasToString(
//                        "http://localhost:" + port
//                                + "/api/v1/recon/breaks/"
//                                + response.getBody().getBreakId());
//
//        // Verify it is actually persisted in the DB
//        assertThat(
//                reconBreakRepository.findById(
//                        response.getBody().getBreakId()))
//                .isPresent();
//    }
//
//    @Test
//    void givenNoCredentials_whenCreateBreak_then401() {
//        ReconBreak request = new ReconBreak();
//        request.setTradeId(23L);
//        request.setBreakType("QUANTITY_MISMATCH");
//        request.setStatus("OPEN");
//
//        ResponseEntity<String> response = restTemplate
//                .postForEntity(
//                        "http://localhost:" + port + "/api/v1/recon/breaks",
//                        request,
//                        String.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
//    }
//
//    // ----------------------------------------------------------------
//    // PUT /api/v1/recon/breaks/{id}/resolve
//    // ----------------------------------------------------------------
//
//    @Test
//    void givenOpsUser_whenResolveBreak_then403() {
//        // Seed a break to resolve
//        ReconBreak seeded = new ReconBreak();
//        seeded.setTradeId(30L);
//        seeded.setBreakType("AMOUNT_MISMATCH");
//        seeded.setStatus("OPEN");
//        ReconBreak saved = reconBreakRepository.save(seeded);
//
//        // ops role should NOT be allowed to resolve — expects 403
//        ResponseEntity<String> response = restTemplate
//                .withBasicAuth("ops", "ops123")
//                .exchange(
//                        "http://localhost:" + port
//                                + "/api/v1/recon/breaks/" + saved.getBreakId() + "/resolve",
//                        org.springframework.http.HttpMethod.PUT,
//                        null,
//                        String.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
//    }
//
//    @Test
//    void givenManagerUser_whenResolveBreak_then200() {
//        // Only a manager/admin role should be able to resolve
//        ReconBreak seeded = new ReconBreak();
//        seeded.setTradeId(31L);
//        seeded.setBreakType("AMOUNT_MISMATCH");
//        seeded.setStatus("OPEN");
//        ReconBreak saved = reconBreakRepository.save(seeded);
//
//        ResponseEntity<ReconBreak> response = restTemplate
//                .withBasicAuth("admin", "admin123")
//                .exchange(
//                        "http://localhost:" + port
//                                + "/api/v1/recon/breaks/" + saved.getBreakId() + "/resolve",
//                        org.springframework.http.HttpMethod.PUT,
//                        null,
//                        ReconBreak.class);
//
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody().getStatus()).isEqualTo("RESOLVED");
//    }
//}