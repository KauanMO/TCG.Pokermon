package rest;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;
import rest.dtos.user.CreateUserDTO;
import rest.dtos.user.LoginDTO;
import rest.dtos.user.OutUserDTO;
import rest.dtos.user.UserInfoDTO;
import services.TokenService;
import services.UserService;

import java.util.List;

@Path("users")
public class UserResource {
    @Inject
    private UserService service;

    @Inject
    TokenService tokenService;

    @Inject
    JsonWebToken jwt;

    @POST
    @PermitAll
    public Response createUser(CreateUserDTO dto) {
        var newUser = service.registerUser(dto);

        return Response
                .ok(new OutUserDTO(newUser))
                .build();
    }

    @POST
    @Path("login")
    @PermitAll
    public Response login(LoginDTO dto) {
        var userFound = service.login(dto);
        String token = tokenService.generateToken(userFound, List.of("USER", "ADMIN"));

        return Response
                .ok(new OutUserDTO(userFound, token))
                .build();
    }

    @GET
    @Path("{userId}")
    @RolesAllowed({"ADMIN"})
    public Response getUserInfo(@PathParam("userId") Long userId, @Context SecurityContext context) {
        var userFound = service.getUserInfo(userId);

        return Response
                .ok(new UserInfoDTO(userFound))
                .build();
    }

    @GET
    @Path("info")
    @Authenticated
    public Response getCurrentUserInfo() {
        var userFound = service.getCurrentUserInfo();

        return Response
                .ok(new UserInfoDTO(userFound))
                .build();
    }
}