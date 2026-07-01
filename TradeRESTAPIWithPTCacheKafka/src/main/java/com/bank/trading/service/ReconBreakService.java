package com.bank.trading.service;


import com.bank.trading.domain.ReconBreak;
import com.bank.trading.dto.BreakSummaryDto;
import com.bank.trading.exception.ReconBreakNotFoundException;
import com.bank.trading.kafka.BreakResolvedPublisher;
import com.bank.trading.repository.ReconBreakRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReconBreakService {

    private final ReconBreakRepository repository;
    private final BreakResolvedPublisher publisher;

    public ReconBreakService(ReconBreakRepository repository, BreakResolvedPublisher publisher) {

        this.repository = repository;
        this.publisher=publisher;
    }

    public List<ReconBreak> getBreaks( String status) {

        return repository.findByStatus(status);
    }

    // for pagination
    public Page<ReconBreak> getBreaks(
            String status,
            Pageable pageable) {

        return repository.findByStatus(
                status,
                pageable);
    }

    public ReconBreak resolveBreak(Integer breakId) {

        ReconBreak reconBreak =
                repository.findById(breakId)
                        .orElseThrow(
                                () -> new ReconBreakNotFoundException(
                                    "Recon break not found: "+    breakId));

        reconBreak.setStatus("RESOLVED");

        // added new

        reconBreak.setResolvedBy("Admin");


        ReconBreak saved =
                repository.save(reconBreak);

        publisher.publish(
                saved.getBreakId(),
                saved.getTradeId());

        return saved;
    }

    public List<ReconBreak> findAll() {

        return repository.findAll();
    }



    public ReconBreak findById(
            Integer id) {

        return repository.findById(id)
                .orElseThrow(
                        () ->
                                new ReconBreakNotFoundException("Recon Break not found"+id));
    }

    public ReconBreak createBreak(ReconBreak reconBreak) {

        reconBreak.setBreakId(null);

        if (reconBreak.getStatus() == null) {

            reconBreak.setStatus("OPEN");
        }

        return repository.save(reconBreak);
    }


    // for chart
    public List<BreakSummaryDto> getBreakSummary() {

        return repository.getBreakSummary()
                .stream()
                .map(row -> new BreakSummaryDto(
                        (Integer) row[0],
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }


}