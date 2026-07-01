//package com.bank.trading.security;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.util.ContentCachingRequestWrapper;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class CounterpartyValidationFilter extends OncePerRequestFilter {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain)
//            throws ServletException, IOException {
//
//        if (!"POST".equalsIgnoreCase(request.getMethod())
//                || !request.getRequestURI().startsWith("/api/v1/trades")) {
//
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        ContentCachingRequestWrapper wrappedRequest =
//                new ContentCachingRequestWrapper(request,Integer.MAX_VALUE);
//
//        try {
//
//            String body = wrappedRequest.getReader()
//                    .lines()
//                    .reduce("", String::concat);
//
//            JsonNode json = objectMapper.readTree(body);
//
//            if (!json.has("counterpartyId")
//                    || json.get("counterpartyId").isNull()
//                    || json.get("counterpartyId").asText().isBlank()) {
//
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                response.setContentType("application/json");
//
//                response.getWriter().write("""
//                        {
//                          "error":"counterpartyId is required"
//                        }
//                        """);
//
//                return;
//            }
//
//        } catch (Exception ex) {
//
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            response.setContentType("application/json");
//
//            response.getWriter().write("""
//                    {
//                      "error":"Invalid JSON request body"
//                    }
//                    """);
//
//            return;
//        }
//
//        filterChain.doFilter(wrappedRequest, response);
//    }
//}



//package com.bank.trading.security;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class CounterpartyValidationFilter
//        extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain)
//            throws ServletException, IOException {
//
//        if ("POST".equals(request.getMethod())
//                && request.getRequestURI()
//                .startsWith("/api/v1/trades")) {
//
//            String body =
//                    request.getReader()
//                            .lines()
//                            .reduce(
//                                    "",
//                                    String::concat);
//
//            ObjectMapper mapper = new ObjectMapper();
//
//            JsonNode json =
//                    mapper.readTree(body);
//
//            if (!json.has("counterpartyId")
//                    || json.get("counterpartyId").isNull()) {
//
////
////            if (!body.contains("counterpartyId")) {
//
//                response.setStatus(
//                        HttpServletResponse.SC_BAD_REQUEST);
//
//                response.setContentType(
//                        "application/json");
//
//                response.getWriter()
//                        .write("""
//                            {
//                              "error":"counterpartyId is required"
//                            }
//                            """);
//
//                return;
//            }
//        }
//
//        filterChain.doFilter(
//                request,
//                response);
//    }
//}