package com.bank.trading.scheduler;

import com.bank.trading.repository.SettlementRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class SettlementMonitoringJob {

    @Autowired
    private SettlementRepository repository;


  //  @Scheduled(fixedRate = 10000)
    @Scheduled(fixedRate = 300000)
    //@Scheduled(cron = "0 */5 * * * *")
    public void logUnsettledTradeCount() {

        long count = repository
                .countByStatusIn(
                        List.of(
                                "PENDING",
                                "UNSETTLED"));

        log.info(
                "UNSETTLED trade count = {}",
                count);
    }
}