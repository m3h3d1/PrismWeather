package com.mehedi.prismweather.factory;

import com.mehedi.prismweather.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Factory for creating standardized API responses.
 * Implements the Factory pattern to centralize response creation.
 */
@Component
public class ResponseFactory {

    /**
     * Creates a success response with OK status.
     *
     * @param message Success message
     * @param data Response data
     * @return ApiResponse with OK status
     */
    public <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates a success response with CREATED status.
     *
     * @param message Success message
     * @param data Response data
     * @return ApiResponse with CREATED status
     */
    public <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .status(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Creates an error response with the specified status.
     *
     * @param status HTTP status code
     * @param message Error message
     * @param data Error data (can be null)
     * @return ApiResponse with error status
     */
    public <T> ApiResponse<T> error(int status, String message, T data) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }
}