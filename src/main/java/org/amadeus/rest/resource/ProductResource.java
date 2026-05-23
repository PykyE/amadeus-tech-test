package org.amadeus.rest.resource;

import io.quarkus.grpc.GrpcClient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.amadeus.*;
import org.amadeus.rest.dto.APIResponseDTO;
import org.amadeus.rest.dto.CreateProductDTO;
import org.amadeus.rest.dto.ProductDTO;
import org.amadeus.rest.dto.UpdateProductDTO;
import org.amadeus.rest.mapper.ProductMapper;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @GrpcClient("product-service")
    ProductService productService;

    @GET
    @Path("/{id}")
    public APIResponseDTO<ProductDTO> getProductById(@PathParam("id") @NotBlank String id) {
        ProductResponse grpcResponse =
                productService
                        .getProduct(ProductId.newBuilder().setId(id).build())
                        .await()
                        .indefinitely();
        return APIResponseDTO.success(ProductMapper.toProductDTO(grpcResponse));
    }

    @GET
    public APIResponseDTO<List<ProductDTO>> getAllProducts() {
        ProductListResponse grpcResponse =
                productService
                        .getAllProducts(EmptyRequest.newBuilder().build())
                        .await()
                        .indefinitely();
        List<ProductDTO> products =
                grpcResponse.getProductsList().stream().map(ProductMapper::toProductDTO).toList();
        return APIResponseDTO.success(products);
    }

    @POST
    public APIResponseDTO<ProductDTO> createProduct(@Valid CreateProductDTO request) {
        CreateProductRequest grpcRequest = ProductMapper.toCreateRequest(request);
        ProductResponse grpcResponse =
                productService.createProduct(grpcRequest).await().indefinitely();
        return APIResponseDTO.success(ProductMapper.toProductDTO(grpcResponse));
    }

    @PUT
    @Path("/{id}")
    public APIResponseDTO<ProductDTO> updateProduct(
            @PathParam("id") @NotBlank String id, @Valid UpdateProductDTO request) {
        ProductResponse grpcResponse =
                productService
                        .updateProduct(ProductMapper.toUpdateRequest(id, request))
                        .await()
                        .indefinitely();
        return APIResponseDTO.success(ProductMapper.toProductDTO(grpcResponse));
    }

    @DELETE
    @Path("/{id}")
    public APIResponseDTO<Void> deleteProduct(@PathParam("id") @NotBlank String id) {
        productService
                .deleteProduct(ProductId.newBuilder().setId(id).build())
                .await()
                .indefinitely();
        return APIResponseDTO.success(null);
    }
}
