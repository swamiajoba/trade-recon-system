package com.bank.trading.kafka;

import com.bank.trading.domain.ReconBreak;
import com.bank.trading.repository.ReconBreakRepository;
import com.bank.trading.repository.TradeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeEventConsumer {

    private final ObjectMapper objectMapper;

    private final ReconBreakRepository repository;

    @KafkaListener(
            topics = "trades.settled",
            groupId = "recon-group")
    public void consume(
            String message) {

        try {

            TradeEvent event =
                    objectMapper.readValue(
                            message,
                            TradeEvent.class);

            log.info(
                    "BREAK DETECTED for {}",
                    event.getTradeId());


            ReconBreak reconBreak =
                    new ReconBreak();

            reconBreak.setTradeId(
                    event.getTradeId());

            reconBreak.setBreakType(
                    "MISSING_TRADE");

            reconBreak.setTheirQty(200);

            reconBreak.setTheirAmount(323232.00);

            reconBreak.setStatus(
                    "OPEN");

        //    reconBreak.setResolvedBy("Admin");

            repository.save(reconBreak);

            log.info(
                    "Recon break created");

        } catch (Exception e) {

            log.error(
                    "Kafka consumer failed",
                    e);
        }
    }
}