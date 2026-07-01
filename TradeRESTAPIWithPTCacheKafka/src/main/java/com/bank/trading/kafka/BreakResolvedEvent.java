package com.bank.trading.kafka;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BreakResolvedEvent {

    private Integer breakId;

    private Long tradeId;

    private Instant resolvedAt;
}