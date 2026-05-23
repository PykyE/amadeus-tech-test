package org.amadeus.rest.mapper;

import org.amadeus.CreateProductRequest;
import org.amadeus.ProductResponse;
import org.amadeus.UpdateProductRequest;
import org.amadeus.rest.dto.CreateProductDTO;
import org.amadeus.rest.dto.ProductDTO;
import org.amadeus.rest.dto.UpdateProductDTO;

public class ProductMapper {

    public static ProductDTO toProductDTO(ProductResponse response) {
        return ProductDTO.fromProductResponse(response);
    }

    public static CreateProductRequest toCreateRequest(CreateProductDTO dto) {
        return CreateProductRequest.newBuilder()
                .setName(dto.name())
                .setPrice(dto.price())
                .setDescription(dto.description())
                .setQuantity(dto.quantity())
                .setTags(dto.tags())
                .build();
    }

    public static UpdateProductRequest toUpdateRequest(String id, UpdateProductDTO dto) {
        return UpdateProductRequest.newBuilder()
                .setId(id)
                .setName(dto.name())
                .setPrice(dto.price())
                .setDescription(dto.description())
                .setQuantity(dto.quantity())
                .setTags(dto.tags())
                .setActive(dto.active())
                .build();
    }
}
