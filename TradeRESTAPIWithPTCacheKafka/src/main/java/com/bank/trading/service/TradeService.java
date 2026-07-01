package com.bank.trading.service;

import com.bank.trading.domain.Trade;
import com.bank.trading.exception.TradeNotFoundException;
import com.bank.trading.kafka.TradeEvent;
import com.bank.trading.kafka.TradeEventPublisher;
import com.bank.trading.repository.TradeRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TradeService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    TradeService.class);

    private final TradeRepository repository;

    private final Counter tradeCounter;
    private final TradeEventPublisher publisher;
    private final  Tracer tracer;
    public TradeService(
            TradeRepository repository,
            MeterRegistry registry , TradeEventPublisher publisher, Tracer tracer) {

        this.repository = repository;
        this.publisher = publisher;
        this.tracer =tracer;
        this.tradeCounter =
                Counter.builder("trades_ingested_total")
                        .description(
                                "Total number of trades ingested")
                        .register(registry);
    }

    @NewSpan("save-trade")
    public Trade createTrade( Trade trade) {

        log.debug("Entering createTrade()");

        log.info(
                "Creating trade for instrument {}",
                trade.getInstrumentId());

        log.warn("Trade quantity unusually large");

        Trade saved= null;

        Span dbSpan =
                tracer.nextSpan()
                        .name("postgres-save-trade")
                        .start();

        try (Tracer.SpanInScope ws =
                     tracer.withSpan(dbSpan)) {

            saved = repository.save(trade);

        } finally {

            dbSpan.end();
        }



        // publish tradeevent
        TradeEvent event =
                TradeEvent.builder()
                        .tradeId(saved.getTradeId())
                        .instrumentId(saved.getInstrumentId())
                        .counterpartyId(saved.getCounterpartyId())
                        .timestamp(Instant.now())
                        .build();

        publisher.publishTradeCreated(event);

        // trade count metrics generate
        tradeCounter.increment();

        //MDC = Mapped Diagnostic Context
        MDC.put(
                "tradeId",
                String.valueOf(
                        saved.getTradeId()));

        MDC.put(
                "action",
                "CREATE_TRADE");

        MDC.put("timestamp",
                Instant.now().toString());

        log.info(
                "Trade created successfully");

        MDC.clear();
        return saved;
    }

    public List<Trade> getAllTrades() {

        return repository.findAll();
    }

    public Trade getTradeById( long tradeId) {

        return repository.findById(tradeId)
                .orElseThrow(
                        () -> new TradeNotFoundException(
                                "Trade not found : "
                                        + tradeId));
    }

    public List<Trade> getTradesByStatus(
            String status) {

        return repository.findByStatus(status);
    }

    public Trade updateTrade(  long tradeId, Trade trade) {
        if(repository.existsById(tradeId)) {
            trade.setTradeId(tradeId);
            return repository.save(trade);
        }else {
            throw new TradeNotFoundException("Trade not found for update");
        }
    }

    public void deleteTrade(long tradeId) {

        repository.deleteById(tradeId);
    }
}