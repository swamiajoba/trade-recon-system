package com.bank.trading.controller;

import com.bank.trading.domain.ReconBreak;
import com.bank.trading.dto.BreakSummaryDto;
import com.bank.trading.service.ReconBreakService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/recon")

public class ReconBreakController {

    private final ReconBreakService service;

    public ReconBreakController(ReconBreakService service) {

        this.service = service;
    }



    @GetMapping("/breaks")
    public List<ReconBreak> getBreaks( @RequestParam String status) {

        return service.getBreaks(status);
    }


    @GetMapping("/breaks/page")
    public Page<ReconBreak> getPagedBreaks(
            @RequestParam String status,
            Pageable pageable) {

        return service.getBreaks(status, pageable);
    }

    @PutMapping("/breaks/{id}/resolve")
    public ResponseEntity<ReconBreak> resolveBreak(@PathVariable Integer id) {

        return ResponseEntity.ok(service.resolveBreak(id));
    }


    @GetMapping("/breaks/{id}")
    public ResponseEntity<ReconBreak> getBreakById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                service.findById(id));
    }

    @PostMapping("/breaks")
    public ResponseEntity<ReconBreak> createBreak(
            @Valid @RequestBody ReconBreak reconBreak) {

        ReconBreak created =
                service.createBreak(reconBreak);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()   // picks up http://localhost:{port}/api/v1/recon/breaks
                .path("/{id}")
                .buildAndExpand(created.getBreakId())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(created);
    }

    @GetMapping("/breaks/chart/summary")
    public List<BreakSummaryDto> getBreakSummary() {

        return service.getBreakSummary();
    }
}