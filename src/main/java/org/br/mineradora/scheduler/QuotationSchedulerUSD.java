package org.br.mineradora.scheduler;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.br.mineradora.service.QuotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class QuotationSchedulerUSD {

    private final Logger LOG = LoggerFactory.getLogger(QuotationSchedulerUSD.class);

    @Inject
    QuotationService quotationService;

    @Transactional
    @Scheduled(every = "20s", identity = "task-job-usd")
    void schedule(){
        LOG.info("-- Executando Scheduler USD-BRL --");
        quotationService.getCurrencyPrice("USD-BRL");
    }

    @Transactional
    @Scheduled(cron = "0 0 23 * * ?", identity = "task-job-usd")
    void scheduleCleanDataBase(){
        LOG.info("-- Executando Scheduler Clean Data Base para USD-BRL --");
        quotationService.cleanDataBase();
    }

}