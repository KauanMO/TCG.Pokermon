package services.exceptions;

import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class GlobalExceptionMapper {
    @ServerExceptionMapper
    public Response exception(NoBalanceEnoughException e) {
        return Response
                .status(400)
                .entity(e.getMessage())
                .build();
    }
}
