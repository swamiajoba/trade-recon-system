package com.bank.trading.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeEventPublisher {

    private static final String TOPIC =
            "trades.settled";

    private final KafkaTemplate<String,String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public void publishTradeCreated(
            TradeEvent event) {

        try {

            String json =
                    objectMapper.writeValueAsString(event);

            kafkaTemplate.send(
                    TOPIC,
                    json);

            log.info(
                    "Published trade event {}",
                    json);

        } catch (Exception e) {

            log.error(
                    "Kafka publish failed",
                    e);
        }
    }
}