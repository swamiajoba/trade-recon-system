package com.bank.trading.controller;

import com.bank.trading.domain.Instrument;
import com.bank.trading.service.InstrumentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/instruments")

public class InstrumentController {

    private final InstrumentService service;

    public InstrumentController(InstrumentService service) {

        this.service = service;
    }

    @GetMapping("/{isin}")
    public Instrument getInstrument(@PathVariable String isin) {

        return service.findByIsin(isin);
    }
}