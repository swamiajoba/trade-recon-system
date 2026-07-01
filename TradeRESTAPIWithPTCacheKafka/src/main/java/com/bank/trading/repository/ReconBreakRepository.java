package com.bank.trading.repository;

import com.bank.trading.domain.ReconBreak;
import com.bank.trading.dto.BreakSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReconBreakRepository extends JpaRepository<ReconBreak, Integer> {

    List<ReconBreak> findByStatus( String status);

    // for pagination
    Page<ReconBreak> findByStatus(String status, Pageable pageable);

    @Query(value = """
    SELECT
        t.counterparty_id as counterpartyId,
        COUNT(*) as breakCount
    FROM recon_breaks b
    JOIN trades t
        ON b.trade_id = t.trade_id
    GROUP BY t.counterparty_id
    """,
            nativeQuery = true)
    List<Object[]> getBreakSummary();
}