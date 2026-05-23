package org.amadeus.rest.dto;

import java.time.LocalDateTime;
import org.amadeus.ProductResponse;

public record ProductDTO(
        String id,
        String name,
        Double price,
        String description,
        Integer quantity,
        LocalDateTime creationDate,
        String tags,
        boolean active) {

    public static ProductDTO fromProductResponse(ProductResponse response) {
        LocalDateTime creationDate =
                response.getCreationDate().isBlank()
                        ? null
                        : LocalDateTime.parse(response.getCreationDate());

        return new ProductDTO(
                response.getId(),
                response.getName(),
                response.getPrice(),
                response.getDescription(),
                (int) response.getQuantity(),
                creationDate,
                response.getTags(),
                response.getActive());
    }
}
