package org.amadeus.rest.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.amadeus.rest.dto.APIResponseDTO;

@Provider
public class GrpcExceptionMapper implements ExceptionMapper<StatusRuntimeException> {

    @Override
    public Response toResponse(StatusRuntimeException exception) {
        Status status = exception.getStatus();

        int httpStatus =
                switch (status.getCode()) {
                    case NOT_FOUND -> 404;
                    case INVALID_ARGUMENT -> 400;
                    case ALREADY_EXISTS -> 409;
                    case UNAUTHENTICATED -> 401;
                    case PERMISSION_DENIED -> 403;
                    case UNAVAILABLE -> 503;
                    default -> 500;
                };

        APIResponseDTO<?> response = APIResponseDTO.error(status, status.getDescription());
        return Response.status(httpStatus).entity(response).build();
    }
}
