package rest.dtos.set;

import java.time.LocalDate;

public record ExternalSetDTO(
        String id,
        String name,
        String series,
        Integer total,
        LocalDate releasedDate,
        ExternalSetImagesDTO images
        ) {
    record ExternalSetImagesDTO(String symbol, String logo) {
    }
}
