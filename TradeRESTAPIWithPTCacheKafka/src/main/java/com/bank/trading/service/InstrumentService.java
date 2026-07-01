package com.bank.trading.service;

import com.bank.trading.domain.Instrument;
import com.bank.trading.repository.InstrumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InstrumentService {

    private final InstrumentRepository repository;

    public InstrumentService(InstrumentRepository repository) {

        this.repository = repository;
    }

    @Cacheable(
            value = "instruments",
            key = "#isin")
    public Instrument findByIsin(String isin) {

        log.info(
                "DATABASE HIT FOR ISIN = "
                        + isin);

        return repository.findByIsin(isin)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Instrument not found"));
    }
}