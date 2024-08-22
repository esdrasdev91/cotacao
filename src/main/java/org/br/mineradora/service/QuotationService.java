package org.br.mineradora.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jackson.Jacksonized;
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

    @Jacksonized
    public void getCurrencyPrice(String pair) {
        List<CurrencyPriceDTO> currencyPriceInfoList = currencyPriceClient.getPriceByPair(pair);
        if (!currencyPriceInfoList.isEmpty()) {
            CurrencyPriceDTO currencyPriceInfo = currencyPriceInfoList.get(0);
            if (currencyPriceInfo != null) {
                if (updateCurrentInfoPrice(currencyPriceInfo, pair)) {
                    kafkaEvents.sendNewKafkaEvent(QuotationDTO
                            .builder()
                            .currencyPrice(new BigDecimal(currencyPriceInfo.getBid()))
                            .date(new Date())
                            .build());
                }
            } else {
                System.err.println("CurrencyPriceDTO está null.");
            }
        } else {
            System.err.println("A lista de CurrencyPriceDTO está vazia.");
        }
    }

    private Boolean updateCurrentInfoPrice(CurrencyPriceDTO currencyPriceInfo, String pair) {
        if (currencyPriceInfo == null || currencyPriceInfo.getBid() == null) {
            System.err.println("CurrencyPriceDTO ou bid está null.");
            return false;
        }

        BigDecimal currentPrice = new BigDecimal(currencyPriceInfo.getBid());
        Boolean updatePrice = false;

        QuotationEntity lastPrice = quotationRepository.find("pair", pair).singleResultOptional().orElse(null);

        if (lastPrice == null) {
            saveQuotation(currencyPriceInfo, pair);
            updatePrice = true;
        } else {
            if (currentPrice.floatValue() > lastPrice.getCurrencyPrice().floatValue()) {
                updatePrice = true;
                saveQuotation(currencyPriceInfo, pair);
            }
        }

        return updatePrice;
    }

    private void saveQuotation(CurrencyPriceDTO currencyInfo, String pair){
        QuotationEntity quotation = new QuotationEntity();

        quotation.setDate(new Date());
        quotation.setCurrencyPrice(new BigDecimal(currencyInfo.getBid()));
        quotation.setPctChange(currencyInfo.getPctChange());
        quotation.setPair(pair);

        quotationRepository.persist(quotation);
        System.err.println("Salvo com sucesso!" + quotation);
    }

}