package org.amadeus.rest.exception;

import io.grpc.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.amadeus.rest.dto.APIResponseDTO;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .findFirst()
                .orElse("Validation failed");

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(APIResponseDTO.error(Status.INVALID_ARGUMENT, message))
                .build();
    }

}

