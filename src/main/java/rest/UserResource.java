package rest;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
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
    private TokenService tokenService;

    @POST
    @PermitAll
    public Response createUser(@Valid CreateUserDTO dto) {
        var newUser = service.registerUser(dto);

        return Response
                .ok(new OutUserDTO(newUser))
                .build();
    }

    @POST
    @Path("login")
    @PermitAll
    public Response login(@Valid LoginDTO dto) {
        var userFound = service.login(dto);
        String token = tokenService.generateToken(userFound);

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

    @PATCH
    @Authenticated
    @Path("pokemon-favorite-code")
    public Response updateFavoritePokemonCode(@QueryParam("pokemonCode") Integer pokemonCode) {
        service.updateUserFavoritePokemonCode(pokemonCode);

        return Response
                .ok()
                .build();
    }
}