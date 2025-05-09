package rest.dtos.deckcard;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateDeckCardDTO(
        @NotNull(message = "O ID do deck deve ser preenchido")
        Long deckId,
        @NotNull(message = "Deve ser preenchida a lista de IDs das novas cartas do deck")
        @NotEmpty(message = "A lista de IDs das novas cartas não pode ser vazia")
        List<Long> cardIds
) { }