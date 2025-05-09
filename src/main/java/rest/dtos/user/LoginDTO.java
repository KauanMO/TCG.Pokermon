package rest.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginDTO(
        @NotNull(message = "O email deve ser preenchido")
        @NotBlank(message = "O email deve ser preenchido")
        String email,
        @NotNull(message = "O email deve ser preenchida")
        @NotBlank(message = "A senha deve ser preenchida")
        String password
) {
}
