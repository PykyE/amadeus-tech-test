package org.amadeus.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateProductDTO(
        @NotBlank(message = "product name is required") String name,
        @NotNull(message = "product price is required") @Positive(message = "productt price must be greater than 0") Double price,
        @NotBlank(message = "product description is required") String description,
        @NotNull(message = "product quantity is required") @PositiveOrZero(message = "product quantity must be 0 or greater") Integer quantity,
        @NotBlank(message = "product tags are required") String tags
) {

}
