package rest.dtos.cardSet;

import java.time.LocalDate;

public record ExternalSetDTO(
        String id,
        String name,
        String series,
        Integer total,
        LocalDate releasedDate,
        ExternalSetImagesDTO images
        ) {
    public record ExternalSetImagesDTO(String symbol, String logo) { }
}
