package org.br.mineradora.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@Jacksonized
@AllArgsConstructor
public class QuotationDTO {

    private Date date;

    private BigDecimal currencyPrice;

}
