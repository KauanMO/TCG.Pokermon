package rest.clients;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import rest.dtos.card.ExternalCardDTO;
import rest.dtos.external.ExternalCardResponseDTO;

import java.util.Set;

@Path("/cards")
@RegisterRestClient(configKey = "tcg-api")
public interface CardsRestClient {
    @GET
    @ClientHeaderParam(name = "X-Api-Key", value = "${external-api.key}")
    ExternalCardResponseDTO getByName(@QueryParam("q") String query, @QueryParam("select") String select);
}