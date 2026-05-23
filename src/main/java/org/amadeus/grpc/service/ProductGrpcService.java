package org.amadeus.grpc.service;

import io.grpc.Status;
import io.quarkus.grpc.GrpcService;

import io.quarkus.logging.Log;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.amadeus.*;
import org.amadeus.grpc.entity.ProductEntity;
import org.amadeus.grpc.repository.ProductRepository;

@GrpcService
@Blocking
public class ProductGrpcService implements ProductService {

    @Inject
    ProductRepository repository;

    @Override
    public Uni<ProductResponse> getProduct(ProductId request) {
        ProductEntity entity = repository.findById(request.getId());
        if (entity == null) {
            return Uni.createFrom().failure(
                    Status.NOT_FOUND
                            .withDescription("Product not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
        return Uni.createFrom().item(
                ProductResponse.newBuilder()
                        .setId(entity.id)
                        .setName(entity.name)
                        .setPrice(entity.price)
                        .setDescription(entity.description)
                        .setQuantity(entity.quantity)
                        .setCreationDate(entity.creationDate.toString())
                        .setTags(entity.tags)
                        .setActive(entity.active)
                        .build()
        ).invoke(Log::info);
    }

    @Override
    public Uni<ProductResponse> createProduct(CreateProductRequest request) {
        return null;
    }

    @Override
    public Uni<ProductResponse> updateProduct(UpdateProductRequest request) {
        return null;
    }

    @Override
    public Uni<EmptyResponse> deleteProduct(ProductId request) {
        return null;
    }
}