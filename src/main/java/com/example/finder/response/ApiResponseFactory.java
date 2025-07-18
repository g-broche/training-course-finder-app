package com.example.finder.response;

import com.example.finder.response.enums.GenericError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseFactory {

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(new ApiResponse<>(true, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return ResponseEntity.ok(new ApiResponse<>(true, message, data));
    }

    public static ResponseEntity<ApiResponse<Void>> success(String message) {
        return ResponseEntity.ok(new ApiResponse<>(true, message, null));
    }

    public static ResponseEntity<ApiResponse<Void>> success(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new ApiResponse<>(true, message, null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data, HttpStatus status) {
        return ResponseEntity.status(status).body(new ApiResponse<>(true, message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String message, T data, HttpStatus status) {
        return ResponseEntity.status(status).body(new ApiResponse<>(false, message, null));
    }

    public static ResponseEntity<ApiResponse<Void>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(new ApiResponse<>(false, message, null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message, T data) {
        return error(message, data, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ApiResponse<Void>> badRequest(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<ApiResponse<Void>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<ApiResponse<Void>> unauthorized(String message) {
        return error(message, HttpStatus.UNAUTHORIZED);
    }

    public static ResponseEntity<ApiResponse<Void>> internalError() {
        return error(GenericError.INTERNAL_ERROR.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity<ApiResponse<Void>> internalError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
