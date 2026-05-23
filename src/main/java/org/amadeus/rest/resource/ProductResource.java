package org.amadeus.rest.resource;

import io.quarkus.grpc.GrpcClient;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.amadeus.ProductId;
import org.amadeus.ProductResponse;
import org.amadeus.ProductService;
import org.amadeus.rest.dto.APIResponseDTO;
import org.amadeus.rest.dto.ProductDTO;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @GrpcClient("product-service")
    ProductService productService;

    @GET
    @Path("/{id}")
    public APIResponseDTO<ProductDTO> get(@Valid @PathParam("id") String id) {
        ProductResponse grpcResponse = productService.getProduct(ProductId.newBuilder().setId(id).build()).await().indefinitely();
        return APIResponseDTO.success(ProductDTO.fromProductResponse(grpcResponse));
    }

}
