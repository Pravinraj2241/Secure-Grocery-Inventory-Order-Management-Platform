# 🛒 Grocery Management System

A full-stack **Grocery Management System** built with **Java (Spring Boot)**, **MySQL**, and a **HTML/CSS/JavaScript** frontend. It manages inventory, products, customers, billing, and order processing through secure, role-based REST APIs.

> Built as a portfolio/resume project demonstrating full-stack development: REST API design, JWT authentication, relational data modeling, CRUD operations, and a responsive frontend consuming those APIs.

---

## ✨ Features

- **Inventory & product management** — add, edit, delete products; organize them by category; automatic **low-stock alerts** based on a configurable reorder level.
- **Customer management** — maintain a customer directory used for billing.
- **Order processing & billing** — build an order from multiple products, automatically compute line subtotals and order totals, **deduct stock in real time**, and generate a printable invoice. Cancelling an order automatically restocks items.
- **Authentication & authorization** — JWT-based login/register with two roles:
  - `ADMIN` — full access, including managing products & categories
  - `STAFF` — manage customers and process orders, read-only on catalog
- **Dashboard** — live summary cards (total products, low-stock count, customers, orders, pending orders, revenue) and a low-stock watchlist.
- **REST API** — clean, versioned-by-resource endpoints (`/api/products`, `/api/customers`, `/api/orders`, ...) with proper HTTP status codes and JSON error responses.
- **Validation & error handling** — request validation (Jakarta Bean Validation) and a global exception handler returning consistent JSON errors.
- **Zero-setup demo mode** — an H2 in-memory profile so the app runs immediately without installing MySQL.
- **Seeded demo data** — default admin/staff logins and sample categories/products created automatically on first run.
- **Unit tests** — JUnit 5 + Mockito tests for the service layer.

---

## 🧱 Tech Stack

| Layer          | Technology                                                        |
|----------------|--------------------------------------------------------------------|
| Language       | Java 17                                                            |
| Backend        | Spring Boot 3, Spring MVC, Spring Data JPA, Spring Security         |
| Auth           | JWT (jjwt)                                                          |
| Database       | MySQL (production/demo), H2 (in-memory, zero-setup demo)           |
| Frontend       | HTML5, CSS3, Vanilla JavaScript (fetch API, no framework)           |
| Build tool     | Maven                                                               |
| Testing        | JUnit 5, Mockito                                                    |
| Other          | Lombok, Jakarta Bean Validation                                     |

---

## 🗂 Project Structure

```
grocery-management-system/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/grocery/management/
│   │   │   ├── config/          # Security, JWT filter/util, CORS
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Request/response DTOs
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── exception/       # Custom exceptions + global handler
│   │   │   ├── repository/      # Spring Data JPA repositories
│   │   │   ├── seeder/          # Startup data seeder (default users + demo catalog)
│   │   │   ├── service/         # Business logic
│   │   │   └── GroceryManagementApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-mysql.properties
│   │       ├── application-h2.properties
│   │       └── static/          # Frontend (HTML/CSS/JS)
│   │           ├── index.html, login.html, dashboard.html,
│   │           │   products.html, customers.html, orders.html
│   │           ├── css/style.css
│   │           └── js/*.js
│   └── test/java/com/grocery/management/service/ProductServiceTest.java
├── docs/
│   └── Grocery-Management-System.postman_collection.json
└── README.md
```

---

## 🧩 Data Model (ER overview)

```
User (id, username, password, email, role)

Category (id, name, description)
   1 ──< Product (id, name, category_id, price, quantity, unit, reorder_level)

Customer (id, name, phone, email, address)
   1 ──< Order (id, customer_id, order_date, status, total_amount)
                1 ──< OrderItem (id, order_id, product_id, quantity, unit_price, subtotal)
```

- One **Category** has many **Products**.
- One **Customer** has many **Orders**.
- One **Order** has many **OrderItems**, each pointing at a **Product**.
- Creating an order deducts `Product.quantity`; cancelling an order restores it.

---

## 🚀 Getting Started

### Prerequisites
- Java 17+ (JDK)
- Maven 3.8+
- MySQL 8+ (optional — see the zero-setup H2 option below)

### Option A — Quick demo with H2 (no database install needed)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

The app starts at **http://localhost:8080** with an in-memory database, pre-seeded with demo users and sample products/categories. Data resets on restart.

### Option B — MySQL (persistent data)

1. Create the database (or let the app create it — see the connection URL):
   ```sql
   CREATE DATABASE grocery_db;
   ```
2. Update credentials in `src/main/resources/application-mysql.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```
3. Run:
   ```bash
   mvn spring-boot:run
   ```
   (`mysql` is the default active profile in `application.properties`.)

The app will be available at **http://localhost:8080**.

### Building a runnable JAR

```bash
mvn clean package
java -jar target/grocery-management-system.jar --spring.profiles.active=h2
```

---

## 🔐 Default Login Credentials

Seeded automatically on first run (see `DataSeeder.java`):

| Username | Password  | Role  |
|----------|-----------|-------|
| admin    | admin123  | ADMIN |
| staff    | staff123  | STAFF |

> ⚠️ Change or remove these before deploying anywhere public. The JWT secret in `application.properties` should also be changed.

---

## 📡 API Documentation

Base URL: `http://localhost:8080/api`

All endpoints except `/api/auth/**` require a header:
```
Authorization: Bearer <jwt_token>
```

### Auth
| Method | Endpoint             | Access | Description               |
|--------|-----------------------|--------|----------------------------|
| POST   | `/auth/register`      | Public | Register a new user       |
| POST   | `/auth/login`         | Public | Log in, returns JWT token  |

### Categories
| Method | Endpoint              | Access       | Description          |
|--------|------------------------|--------------|-----------------------|
| GET    | `/categories`          | Authenticated| List all categories   |
| GET    | `/categories/{id}`     | Authenticated| Get one category      |
| POST   | `/categories`          | ADMIN        | Create category        |
| PUT    | `/categories/{id}`     | ADMIN        | Update category        |
| DELETE | `/categories/{id}`     | ADMIN        | Delete category        |

### Products
| Method | Endpoint                     | Access        | Description                       |
|--------|-------------------------------|---------------|------------------------------------|
| GET    | `/products`                   | Authenticated | List all products                  |
| GET    | `/products?search=milk`       | Authenticated | Search products by name            |
| GET    | `/products/low-stock`         | Authenticated | List products at/below reorder level |
| GET    | `/products/{id}`               | Authenticated | Get one product                    |
| POST   | `/products`                    | ADMIN         | Create product                     |
| PUT    | `/products/{id}`               | ADMIN         | Update product                     |
| DELETE | `/products/{id}`               | ADMIN         | Delete product                     |

### Customers
| Method | Endpoint                     | Access        | Description             |
|--------|-------------------------------|---------------|--------------------------|
| GET    | `/customers`                  | Authenticated | List all customers        |
| GET    | `/customers?search=priya`     | Authenticated | Search customers by name  |
| GET    | `/customers/{id}`               | Authenticated | Get one customer          |
| POST   | `/customers`                    | Authenticated | Create customer            |
| PUT    | `/customers/{id}`               | Authenticated | Update customer            |
| DELETE | `/customers/{id}`               | Authenticated | Delete customer            |

### Orders & Billing
| Method | Endpoint                          | Access        | Description                                |
|--------|------------------------------------|---------------|----------------------------------------------|
| GET    | `/orders`                          | Authenticated | List all orders (newest first)               |
| GET    | `/orders/{id}`                     | Authenticated | Get one order with items                     |
| GET    | `/orders/customer/{customerId}`    | Authenticated | List orders for a specific customer           |
| POST   | `/orders`                          | Authenticated | Create an order (deducts stock automatically)  |
| PUT    | `/orders/{id}/status`              | Authenticated | Update status (`PENDING`/`COMPLETED`/`CANCELLED`) |
| GET    | `/orders/{id}/invoice`             | Authenticated | Plain-text printable invoice                  |

### Dashboard
| Method | Endpoint              | Access        | Description                        |
|--------|------------------------|---------------|--------------------------------------|
| GET    | `/dashboard/summary`   | Authenticated | Totals: products, customers, orders, revenue |

**Example — Login**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Example — Create an order**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"customerId":1,"items":[{"productId":1,"quantity":3},{"productId":3,"quantity":2}]}'
```

A ready-to-import Postman collection covering every endpoint (with example requests) is provided at:
`docs/Grocery-Management-System.postman_collection.json`

---

## 🖥️ Frontend

Plain HTML/CSS/JS served directly by Spring Boot from `src/main/resources/static`, so the whole app (API + UI) runs from a single `http://localhost:8080` — no separate frontend server needed.

- `login.html` — login & registration
- `dashboard.html` — summary stats + low-stock watchlist
- `products.html` — product CRUD (Admin-only write access)
- `customers.html` — customer CRUD
- `orders.html` — order builder, status updates, printable invoice

The JWT is stored in `localStorage` and attached to every API call via a small `fetch` wrapper in `js/common.js`.

---

## 🧪 Testing

Run the test suite with:
```bash
mvn test
```

`ProductServiceTest` demonstrates unit testing the service layer with Mockito (mocking repositories, verifying save calls, and asserting exceptions for not-found/invalid input).

---

## 🔮 Future Enhancements

- Pagination & sorting on list endpoints
- Refresh tokens / token blacklist on logout
- Supplier management & purchase orders
- Sales reports/analytics (daily/monthly revenue charts)
- Dockerfile + docker-compose for one-command local setup
- Role management UI for Admins to promote/demote users

---

## 📄 License

Released under the [MIT License](LICENSE) — free to use, modify, and include in your own portfolio.

---

## 👤 Author

Built as a personal/portfolio project to demonstrate full-stack Java development skills: Spring Boot, REST API design, JWT security, relational data modeling, and vanilla JS frontend integration.
