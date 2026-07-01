package com.bank.trading.repository;

import com.bank.trading.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Integer> {

    long countByStatusIn( java.util.List<String> statuses);
}