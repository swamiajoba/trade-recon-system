package com.bank.trading.kafka;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeEvent {

    private Long tradeId;

    private Integer instrumentId;

    private Integer counterpartyId;

    private Instant timestamp;
}