package org.amadeus.rest.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.ws.rs.core.Response;
import org.amadeus.rest.dto.APIResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GrpcExceptionMapper Tests")
class GrpcExceptionMapperTest {

    private final GrpcExceptionMapper mapper = new GrpcExceptionMapper();

    @Test
    @DisplayName("should map NOT_FOUND status to 404 HTTP status")
    void testNotFoundMapping() {
        StatusRuntimeException exception = Status.NOT_FOUND
                .withDescription("Product not found")
                .asRuntimeException();

        try (Response response = mapper.toResponse(exception)) {
            assertEquals(404, response.getStatus());
            assertInstanceOf(APIResponseDTO.class, response.getEntity());
            APIResponseDTO<?> dto = (APIResponseDTO<?>) response.getEntity();
            assertEquals("NOT_FOUND", dto.grpcCode());
            assertEquals("Product not found", dto.message());
            assertNull(dto.data());
        }
    }

    @Test
    @DisplayName("should map INVALID_ARGUMENT status to 400 HTTP status")
    void testInvalidArgumentMapping() {
        StatusRuntimeException exception = Status.INVALID_ARGUMENT
                .withDescription("Invalid input: price must be positive")
                .asRuntimeException();

        try (Response response = mapper.toResponse(exception)) {
            assertEquals(400, response.getStatus());
            APIResponseDTO<?> dto = (APIResponseDTO<?>) response.getEntity();
            assertEquals("INVALID_ARGUMENT", dto.grpcCode());
        }
    }

    @Test
    @DisplayName("should map ALREADY_EXISTS status to 409 HTTP status")
    void testAlreadyExistsMapping() {
        StatusRuntimeException exception = Status.ALREADY_EXISTS
                .withDescription("Product already exists")
                .asRuntimeException();

        try (Response response = mapper.toResponse(exception)) {
            assertEquals(409, response.getStatus());
        }
    }

    @Test
    @DisplayName("should map UNAUTHENTICATED status to 401 HTTP status")
    void testUnauthenticatedMapping() {
        StatusRuntimeException exception = Status.UNAUTHENTICATED
                .withDescription("Missing authentication")
                .asRuntimeException();

        try (Response response = mapper.toResponse(exception)) {
            assertEquals(401, response.getStatus());
        }
    }

    @Test
    @DisplayName("should map PERMISSION_DENIED status to 403 HTTP status")
    void testPermissionDeniedMapping() {
        StatusRuntimeException exception = Status.PERMISSION_DENIED
                .withDescription("Access denied")
                .asRuntimeException();

        try (Response response = mapper.toResponse(exception)) {
            assertEquals(403, response.getStatus());
        }
    }

    @Test
    @DisplayName("should map UNAVAILABLE status to 503 HTTP status")
    void testUnavailableMapping() {
        StatusRuntimeException exception = Status.UNAVAILABLE
                .withDescription("Service unavailable")
                .asRuntimeException();

        try (Response response = mapper.toResponse(exception)) {
            assertEquals(503, response.getStatus());
        }
    }

    @Test
    @DisplayName("should map unknown status to 500 HTTP status")
    void testUnknownStatusMapping() {
        StatusRuntimeException exception = Status.UNKNOWN
                .withDescription("An internal error occurred")
                .asRuntimeException();

        try (Response response = mapper.toResponse(exception)) {
            assertEquals(500, response.getStatus());
        }
    }

    @Test
    @DisplayName("should include status description in response message")
    void testStatusDescriptionIncluded() {
        String description = "Custom error message";
        StatusRuntimeException exception = Status.INVALID_ARGUMENT
                .withDescription(description)
                .asRuntimeException();

        try (Response response = mapper.toResponse(exception)) {
            APIResponseDTO<?> dto = (APIResponseDTO<?>) response.getEntity();
            assertEquals(description, dto.message());
        }
    }
}



