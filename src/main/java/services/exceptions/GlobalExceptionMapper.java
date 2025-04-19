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
        return Response
                .status(500)
                .entity(e.getMessage())
                .build();
    }

    @ServerExceptionMapper
    public Response exception(UserWithoutRoleException e) {
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(e.getMessage())
                .build();
    }

    @ServerExceptionMapper
    public Response exception(DuplicatedUniqueEntityException e) {
        return Response
                .status(Response.Status.CONFLICT)
                .entity(e.getMessage())
                .build();
    }

    @ServerExceptionMapper
    public Response exception(ExternalContentNotFoundException e) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(e.getMessage())
                .build();
    }

    @ServerExceptionMapper
    public Response exception(UserNotFoundException e) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(e.getMessage())
                .build();
    }

    @ServerExceptionMapper
    public Response exception(Exception e) {
        System.out.println(e.getMessage());

        return Response
                .status(500)
                .entity(e.getMessage())
                .build();
    }
}
