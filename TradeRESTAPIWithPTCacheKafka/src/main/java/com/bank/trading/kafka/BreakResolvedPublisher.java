package com.bank.trading.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BreakResolvedPublisher {

    private final KafkaTemplate<String,String> kafkaTemplate;

    public void publish(
            Integer breakId,
            Long tradeId) {

        String message = """
                        {
                          "breakId": %d,
                          "tradeId": %d,
                          "status": "RESOLVED",
                          "eventType": "BREAK_RESOLVED"
                        }
                        """.formatted(breakId, tradeId);

        kafkaTemplate.send(
                "recon.breaks.resolved",
                message);

        log.info(
                "Published break resolved event {}",
                breakId);
    }
}