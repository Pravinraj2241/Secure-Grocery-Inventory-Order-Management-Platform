# Grocery Management System

A full-stack Grocery Management System built with Java (Spring Boot), MySQL, and a HTML/CSS/JavaScript frontend. It manages inventory, products, customers, billing, and order processing through secure, role-based REST APIs.

## Features

- Inventory & product management
- Customer management
- Order processing & billing
- Authentication & authorization (JWT)
- Dashboard
- REST API
- Validation & error handling
- Zero-setup demo mode (H2)
- Seeded demo data
- Unit tests

## Tech Stack

- Language: Java 17
- Backend: Spring Boot 3, Spring MVC, Spring Data JPA, Spring Security
- Auth: JWT (jjwt)
- Database: MySQL / H2
- Frontend: HTML5, CSS3, Vanilla JavaScript
- Build tool: Maven

## Getting Started

### Prerequisites
- Java 17+ (JDK)
- Maven 3.8+

### Run with H2 (no database install needed)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

The app starts at http://localhost:8080

## Default Login Credentials

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| staff | staff123 | STAFF |

## API Documentation

Base URL: http://localhost:8080/api

## Frontend

- login.html — login & registration
- dashboard.html — summary stats + low-stock watchlist
- products.html — product CRUD
- customers.html — customer CRUD
- orders.html — order builder, status updates, printable invoice

## Testing

```bash
mvn test
```

## License

MIT License

## Author

Built as a personal/portfolio project.