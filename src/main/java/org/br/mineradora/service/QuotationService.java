package org.br.mineradora.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.br.mineradora.client.CurrencyPriceClient;
import org.br.mineradora.dto.CurrencyPriceDTO;
import org.br.mineradora.dto.QuotationDTO;
import org.br.mineradora.entity.QuotationEntity;
import org.br.mineradora.message.KafkaEvents;
import org.br.mineradora.repository.QuotationRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class QuotationService {

    @Inject
    @RestClient
    CurrencyPriceClient currencyPriceClient;

    @Inject
    QuotationRepository quotationRepository;

    @Inject
    KafkaEvents kafkaEvents;

    public void getCurrencyPrice() {
        CurrencyPriceDTO currencyPriceInfo = currencyPriceClient.getPriceByPair("USD-BRL");
        if (currencyPriceInfo != null && currencyPriceInfo.getUSDBRL() != null) {
            if (updateCurrentInfoPrice(currencyPriceInfo)) {
                kafkaEvents.sendNewKafkaEvent(QuotationDTO
                        .builder()
                        .currencyPrice(new BigDecimal(currencyPriceInfo.getUSDBRL().getBid()))
                        .date(new Date())
                        .build());
            }
        } else {
            System.err.println("CurrencyPriceDTO ou USDBRL está null.");
        }
    }

    private boolean updateCurrentInfoPrice(CurrencyPriceDTO currencyPriceInfo) {
        if (currencyPriceInfo == null || currencyPriceInfo.getUSDBRL() == null) {
            System.err.println("CurrencyPriceDTO ou USDBRL está null.");
            return false;
        }

        BigDecimal currentPrice = new BigDecimal(currencyPriceInfo.getUSDBRL().getBid());
        boolean updatePrice = false;

        List<QuotationEntity> quotationList = quotationRepository.findAll().list();

        if (quotationList.isEmpty()) {
            saveQuotation(currencyPriceInfo);
            updatePrice = true;
        } else {
            QuotationEntity lastDollarPrice = quotationList.get(quotationList.size() - 1);

            if (currentPrice.floatValue() > lastDollarPrice.getCurrencyPrice().floatValue()) {
                updatePrice = true;
                saveQuotation(currencyPriceInfo);
            }
        }

        return updatePrice;
    }

    private void saveQuotation(CurrencyPriceDTO currencyInfo){
        QuotationEntity quotation = new QuotationEntity();

        quotation.setDate(new Date());
        quotation.setCurrencyPrice(new BigDecimal(currencyInfo.getUSDBRL().getBid()));
        quotation.setPctChange(currencyInfo.getUSDBRL().getPctChange());
        quotation.setPair("USD-BRL");

        quotationRepository.persist(quotation);
    }

}
