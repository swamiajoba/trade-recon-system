package com.bank.trading.repository;

import com.bank.trading.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByStatus(String status);
}