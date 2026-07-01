package com.bank.trading.repository;

import com.bank.trading.domain.Instrument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstrumentRepository extends JpaRepository<Instrument,Integer> {
    Optional<Instrument> findByIsin(String isin);
}