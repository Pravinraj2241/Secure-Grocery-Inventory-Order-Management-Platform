package com.grocery.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Generic wrapper used for simple success / error messages returned by the API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {
    private boolean success;
    private String message;
    private LocalDateTime timestamp;

    public static ApiResponse of(boolean success, String message) {
        return ApiResponse.builder()
                .success(success)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
