package rest.dtos.deck;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDeckDTO(
        @NotNull(message = "O nome do deck deve ser preenchido")
        @NotBlank(message = "O nome do deck não pode ser vazio")
        String name
) {
}
