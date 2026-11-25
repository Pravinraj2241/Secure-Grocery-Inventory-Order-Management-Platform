package com.grocery.management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CustomerDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerRequest {
        @NotBlank(message = "Customer name is required")
        private String name;

        @NotBlank(message = "Phone number is required")
        private String phone;

        @Email(message = "Email must be valid")
        private String email;

        private String address;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CustomerResponse {
        private Long id;
        private String name;
        private String phone;
        private String email;
        private String address;
    }
}
