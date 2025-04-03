package services.exceptions;

import io.netty.handler.codec.http.HttpResponseStatus;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.ClientWebApplicationException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class GlobalExceptionMapper {
    @ServerExceptionMapper
    public Response exception(DeckNotFoundException e) {
        return Response
                .status(400)
                .entity(e.getMessage())
                .build();
    }

    @ServerExceptionMapper
    public Response exception(DeckCardValidationException e) {
        return Response
                .status(400)
                .entity(e.getMessage())
                .build();
    }

    @ServerExceptionMapper
    public Response exception(NoBalanceEnoughException e) {
        return Response
                .status(400)
                .entity(e.getMessage())
                .build();
    }

    @ServerExceptionMapper
    public Response exception(IncorrectPasswordException e) {
        return Response
                .status(HttpResponseStatus.UNAUTHORIZED.code())
                .entity(e.getMessage())
                .build();
    }

    @ServerExceptionMapper
    public Response exception(ClientWebApplicationException e) {
        System.out.println(e.getMessage());

        return Response
                .status(500)
                .entity(e.getMessage())
                .build();
    }
}
