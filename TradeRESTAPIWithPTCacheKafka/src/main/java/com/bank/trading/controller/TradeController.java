package com.bank.trading.controller;

import com.bank.trading.domain.Trade;
import com.bank.trading.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trades")
public class TradeController {

    private final TradeService service;

    public TradeController(
            TradeService service) {

        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Trade> createTrade(@Valid @RequestBody Trade trade) {
        Trade newTrade = service.createTrade(trade);
        URI location = URI.create("/api/v1/trades/" + newTrade.getTradeId());

        return ResponseEntity.created(location).body(newTrade);
    }

    @GetMapping
    public List<Trade> getAllTrades() {

        return service.getAllTrades();
    }

    @GetMapping("/{tradeId}")
    public Trade getTradeById( @PathVariable long tradeId) {

        return service.getTradeById(tradeId);
    }

    @GetMapping("/status/{status}")
    public List<Trade> getTradesByStatus( @PathVariable String status) {

        return service.getTradesByStatus(status);
    }

    @PutMapping("/{tradeId}")
    public Trade updateTrade( @PathVariable long tradeId, @RequestBody Trade trade) {

        return service.updateTrade(tradeId, trade);


    }

    @DeleteMapping("/{tradeId}")
    public String deleteTrade( @PathVariable long tradeId) {

        service.deleteTrade(tradeId);

        return "Trade deleted successfully";
    }
}