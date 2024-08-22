package org.br.mineradora.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.br.mineradora.dto.CurrencyPriceDTO;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/{pair}")
@RegisterRestClient
@ApplicationScoped
public interface CurrencyPriceClient {

    @GET
    List<CurrencyPriceDTO> getPriceByPair(@PathParam("pair") String pair);

}
