package rest.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserDTO(
        @NotNull(message = "O nome de usuário deve ser preenchido")
        @NotBlank(message = "O nome de usuário não pode ser vazio")
        String username,
        @NotNull(message = "O email deve ser preenchido")
        @NotBlank(message = "O email não pode ser vazio")
        String email,
        @NotNull
        @Size(message = "A senha deve ter no mínimo 6 caracteres", min = 6)
        String password
) {
}
