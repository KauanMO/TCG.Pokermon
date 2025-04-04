package rest.clients;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import rest.dtos.set.ExternalSetDTO;

@RegisterRestClient(configKey = "tcg-api")
@Path("sets")
public interface SetsRestClient {
    @GET
    @ClientHeaderParam(name = "X-Api-Key", value = "${external-api.key}")
    ExternalSetDTO get(@QueryParam("q") String query, @QueryParam("select") String select);
}
