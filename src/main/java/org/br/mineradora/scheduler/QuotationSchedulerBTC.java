package org.br.mineradora.scheduler;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.br.mineradora.service.QuotationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class QuotationSchedulerBTC {

    private final Logger LOG = LoggerFactory.getLogger(QuotationSchedulerBTC.class);

    @Inject
    QuotationService quotationService;

    @Transactional
    @Scheduled(every = "20s", identity = "task-job-btc")
    void schedule(){
        LOG.info("-- Executando Scheduler BTC-BRL --");
        quotationService.getCurrencyPrice("BTC-BRL");
    }

}
