package org.amadeus.grpc.service;

import io.grpc.StatusRuntimeException;
import org.amadeus.*;
import org.amadeus.grpc.entity.ProductEntity;
import org.amadeus.grpc.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@DisplayName("ProductGrpcService Tests")
class ProductGrpcServiceTest {

    ProductRepository repository;

    ProductGrpcService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(ProductRepository.class);
        service = new ProductGrpcService();
        service.repository = repository;
    }

    @Test
    void testGetProduct_Success() {
        ProductEntity entity = new ProductEntity();
        entity.id = "11111111-1111-1111-1111-111111111111";
        entity.name = "Test";
        entity.price = 9.99D;
        entity.description = "desc";
        entity.quantity = 5;
        entity.creationDate = LocalDateTime.now();
        entity.tags = "tag";
        entity.active = true;

        when(repository.findById("1")).thenReturn(entity);

        ProductResponse resp = service.getProduct(ProductId.newBuilder().setId("1").build())
                .await().indefinitely();

        assertNotNull(resp);
        assertEquals("11111111-1111-1111-1111-111111111111", resp.getId());
        assertEquals("Test", resp.getName());
    }

    @Test
    void testGetProduct_NotFound() {
        when(repository.findById("missing")).thenReturn(null);

        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () ->
                service.getProduct(ProductId.newBuilder().setId("missing").build()).await().indefinitely()
        );

        assertEquals(io.grpc.Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
    }

    @Test
    void testGetAllProducts() {
        ProductEntity e1 = new ProductEntity();
        e1.id = "11111111-1111-1111-1111-111111111111"; e1.name = "A"; e1.price = 1.0D; e1.quantity = 1; e1.creationDate = LocalDateTime.now(); e1.active = true;
        ProductEntity e2 = new ProductEntity();
        e2.id = "11111111-1111-1111-1111-111111111112"; e2.name = "B"; e2.price = 2.0D; e2.quantity = 2; e2.creationDate = LocalDateTime.now(); e2.active = true;

        when(repository.listAll()).thenReturn(List.of(e1, e2));

        ProductListResponse resp = service.getAllProducts(EmptyRequest.newBuilder().build())
                .await().indefinitely();

        assertNotNull(resp);
        assertEquals(2, resp.getProductsCount());
    }

    @Test
    void testCreateProduct() {
        doNothing().when(repository).persist(any(ProductEntity.class));

        CreateProductRequest req = CreateProductRequest.newBuilder()
                .setName("New")
                .setPrice(5.5)
                .setDescription("d")
                .setQuantity(3)
                .setTags("t")
                .build();

        ProductResponse resp = service.createProduct(req).await().indefinitely();

        assertNotNull(resp);
        assertEquals("New", resp.getName());
    }

    @Test
    void testUpdateProduct_Success() {
        ProductEntity entity = new ProductEntity();
        entity.id = "11111111-1111-1111-1111-111111111111"; entity.name = "Old"; entity.price = 1.0D; entity.quantity = 1; entity.creationDate = LocalDateTime.now(); entity.active = true;
        when(repository.findById("11111111-1111-1111-1111-111111111111")).thenReturn(entity);

        UpdateProductRequest req = UpdateProductRequest.newBuilder()
                .setId("11111111-1111-1111-1111-111111111111")
                .setName("NewName")
                .build();

        ProductResponse resp = service.updateProduct(req).await().indefinitely();
        assertNotNull(resp);
        assertEquals("11111111-1111-1111-1111-111111111111", resp.getId());
        assertEquals("NewName", resp.getName());
    }

    @Test
    void testUpdateProduct_NotFound() {
        when(repository.findById("nx")).thenReturn(null);

        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () ->
                service.updateProduct(UpdateProductRequest.newBuilder().setId("nx").build()).await().indefinitely()
        );

        assertEquals(io.grpc.Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
    }

    @Test
    void testDeleteProduct_Success() {
        ProductEntity entity = new ProductEntity();
        entity.id = "11111111-1111-1111-1111-111111111111"; entity.name = "X"; entity.creationDate = LocalDateTime.now(); entity.active = true;
        when(repository.findById("d1")).thenReturn(entity);
        doNothing().when(repository).delete(entity);

        EmptyResponse resp = service.deleteProduct(ProductId.newBuilder().setId("d1").build()).await().indefinitely();
        assertNotNull(resp);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(repository.findById("nx")).thenReturn(null);

        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () ->
                service.deleteProduct(ProductId.newBuilder().setId("nx").build()).await().indefinitely()
        );

        assertEquals(io.grpc.Status.NOT_FOUND.getCode(), ex.getStatus().getCode());
    }
}


