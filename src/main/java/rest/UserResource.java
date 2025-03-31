package rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import rest.dtos.user.CreateUserDTO;
import rest.dtos.user.OutUserDTO;
import services.UserService;

@Path("users")
public class UserResource {
    @Inject
    private UserService service;

    @POST
    public Response createUser(CreateUserDTO dto) {
        var newUser = service.registerUser(dto);

        return Response
                .ok(new OutUserDTO(newUser))
                .build();
    }
}