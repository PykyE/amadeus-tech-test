package org.amadeus.rest.exception;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import java.util.HashSet;
import java.util.Set;
import org.amadeus.rest.dto.APIResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ValidationExceptionMapper Tests")
class ValidationExceptionMapperTest {

    private final ValidationExceptionMapper mapper = new ValidationExceptionMapper();

    @Test
    @DisplayName("should map ConstraintViolationException to 400 HTTP status")
    void testValidationExceptionMapping() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        try (Response response = mapper.toResponse(exception)) {
            assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
            assertInstanceOf(APIResponseDTO.class, response.getEntity());
        }
    }

    @Test
    @DisplayName("should use INVALID_ARGUMENT as gRPC code")
    void testGrpcCodeIsInvalidArgument() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        try (Response response = mapper.toResponse(exception)) {
            APIResponseDTO<?> dto = (APIResponseDTO<?>) response.getEntity();
            assertEquals("INVALID_ARGUMENT", dto.grpcCode());
        }
    }

    @Test
    @DisplayName("should return 'Validation failed' when no violations present")
    void testEmptyViolations() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        try (Response response = mapper.toResponse(exception)) {
            APIResponseDTO<?> dto = (APIResponseDTO<?>) response.getEntity();
            assertEquals("Validation failed", dto.message());
        }
    }

    @Test
    @DisplayName(
            "should map ConstraintViolationException to 400 status with proper response structure")
    void testValidationExceptionStructure() {
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        ConstraintViolationException exception = new ConstraintViolationException(violations);

        try (Response response = mapper.toResponse(exception)) {
            assertEquals(400, response.getStatus());
            APIResponseDTO<?> dto = (APIResponseDTO<?>) response.getEntity();
            assertNotNull(dto);
            assertEquals("INVALID_ARGUMENT", dto.grpcCode());
            assertNotNull(dto.message());
            assertNull(dto.data());
        }
    }
}
