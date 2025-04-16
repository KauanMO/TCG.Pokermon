package rest.clients;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import rest.dtos.cardSet.ExternalSetDTO;
import rest.dtos.external.ExternalSetResponseDTO;

@RegisterRestClient(configKey = "tcg-api")
@Path("sets")
public interface SetsRestClient {
    @GET
    @ClientHeaderParam(name = "X-Api-Key", value = "${external-api.key}")
    ExternalSetResponseDTO get(@QueryParam("q") String query, @QueryParam("select") String select);
}
