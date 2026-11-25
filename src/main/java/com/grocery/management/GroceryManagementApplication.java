package com.grocery.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Grocery Management System.
 *
 * This is a full-stack application that manages:
 *  - Products & Categories (inventory)
 *  - Customers
 *  - Orders & Billing
 *  - User authentication (JWT based) with role based access (ADMIN / STAFF)
 */
@SpringBootApplication
public class GroceryManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroceryManagementApplication.class, args);
    }
}
