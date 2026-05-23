package org.amadeus.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.grpc.Status;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record APIResponseDTO<T>(String grpcCode, String message, T data) {

    public static <T> APIResponseDTO<T> success(T data) {
        return new APIResponseDTO<>("OK", null, data);
    }

    public static <T> APIResponseDTO<T> error(Status status, String message) {
        Status safeStatus = status == null ? Status.UNKNOWN : status;
        String resolvedMessage =
                (message == null || message.isBlank()) ? safeStatus.getDescription() : message;

        if (resolvedMessage == null || resolvedMessage.isBlank()) {
            resolvedMessage = "Unexpected error";
        }

        return new APIResponseDTO<>(safeStatus.getCode().toString(), resolvedMessage, null);
    }
}
