package com.grocery.management.entity;

/**
 * User roles.
 * ADMIN - full access (manage products, categories, users, view reports)
 * STAFF - can manage customers and create/process orders, view products
 */
public enum Role {
    ADMIN,
    STAFF
}
