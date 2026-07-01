package com.bank.trading.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor                          // Lombok injects both fields
public class BreakResolvedConsumer {

    private final SimpMessagingTemplate messagingTemplate;  // ← add this

    @KafkaListener(
            topics  = "recon.breaks.resolved",
            groupId = "recon-group")
    public void consume(String message) {

        log.info("BREAK RESOLVED EVENT RECEIVED : {}", message);

        // Forward raw JSON string directly to all browser clients
        // message is already: { "breakId": 7, "tradeId": 101 }
        messagingTemplate.convertAndSend(
                "/topic/breaks",
                message
        );

        log.info("Forwarded to WebSocket /topic/breaks");
    }
}