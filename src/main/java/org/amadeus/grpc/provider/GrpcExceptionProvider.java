package org.amadeus.grpc.provider;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GrpcExceptionProvider implements ExceptionMapper<StatusRuntimeException> {

    @Override
    public Response toResponse(StatusRuntimeException exception) {
        Status status = exception.getStatus();

        return switch (status.getCode()) {
            case NOT_FOUND -> Response.status(404).entity("Not found").build();
            case INVALID_ARGUMENT -> Response.status(400).entity("Bad request").build();
            case ALREADY_EXISTS -> Response.status(409).entity("Conflict").build();
            case UNAUTHENTICATED -> Response.status(401).build();
            case PERMISSION_DENIED -> Response.status(403).build();
            default -> Response.status(500).entity("Internal error").build();
        };
    }
}