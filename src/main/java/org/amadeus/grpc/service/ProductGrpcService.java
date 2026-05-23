package org.amadeus.grpc.service;

import io.grpc.Status;
import io.quarkus.grpc.GrpcService;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.amadeus.*;
import org.amadeus.grpc.entity.ProductEntity;
import org.amadeus.grpc.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;

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
        return Uni.createFrom().item(() -> toResponse(entity));
    }

    @Override
    public Uni<ProductListResponse> getAllProducts(EmptyRequest request) {
        List<ProductResponse> products = repository.listAll().stream()
                .map(this::toResponse)
                .toList();

        return Uni.createFrom().item(() ->
                ProductListResponse.newBuilder()
                        .addAllProducts(products)
                        .build()
        );
    }

    @Override
    @Transactional
    public Uni<ProductResponse> createProduct(CreateProductRequest request) {
        ProductEntity entity = new ProductEntity();
        entity.name = request.getName();
        entity.price = request.getPrice();
        entity.description = request.getDescription();
        entity.quantity = Math.toIntExact(request.getQuantity());
        entity.tags = request.getTags();
        entity.creationDate = LocalDateTime.now();
        entity.active = true;
        repository.persist(entity);

        return Uni.createFrom().item(() -> toResponse(entity));
    }

    @Override
    @Transactional
    public Uni<ProductResponse> updateProduct(UpdateProductRequest request) {
        ProductEntity entity = repository.findById(request.getId());
        if (entity == null) {
            return Uni.createFrom().failure(
                    Status.NOT_FOUND
                            .withDescription("Product not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
        entity.name = request.getName();
        entity.price = request.getPrice();
        entity.description = request.getDescription();
        entity.quantity = (int) request.getQuantity();
        entity.tags = request.getTags();
        entity.active = request.getActive();

        ProductResponse response = toResponse(entity);
        return Uni.createFrom().item(response);
    }

    @Override
    @Transactional
    public Uni<EmptyResponse> deleteProduct(ProductId request) {
        ProductEntity entity = repository.findById(request.getId());
        if (entity == null) {
            return Uni.createFrom().failure(
                    Status.NOT_FOUND
                            .withDescription("Product not found with id: " + request.getId())
                            .asRuntimeException()
            );
        }
        repository.delete(entity);
        return Uni.createFrom().item(EmptyResponse.newBuilder().build());
    }

    private ProductResponse toResponse(ProductEntity entity) {
        return ProductResponse.newBuilder()
                .setId(entity.id == null ? "" : entity.id)
                .setName(entity.name == null ? "" : entity.name)
                .setPrice(entity.price == null ? 0D : entity.price)
                .setDescription(entity.description == null ? "" : entity.description)
                .setQuantity(entity.quantity == null ? 0 : entity.quantity)
                .setCreationDate(entity.creationDate == null ? "" : entity.creationDate.toString())
                .setTags(entity.tags == null ? "" : entity.tags)
                .setActive(entity.active)
                .build();
    }
}