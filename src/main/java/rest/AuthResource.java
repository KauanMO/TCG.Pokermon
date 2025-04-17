package rest;

import io.quarkus.security.Authenticated;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("auth")
public class AuthResource {
    @GET
    @Authenticated
    public Response checkToken() {
        return Response.ok().build();
    }
}