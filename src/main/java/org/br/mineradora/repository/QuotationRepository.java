package org.br.mineradora.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.br.mineradora.entity.QuotationEntity;

@ApplicationScoped
public class QuotationRepository implements PanacheRepository<QuotationEntity> {

    public QuotationEntity findLastByPair(String pair) {
        return find("pair = ?1 order by date desc", pair).firstResult();
    }

}
