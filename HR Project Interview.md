# Grocery Management System - Complete Guide

## 📖 Project Overview

A full-stack **Grocery Management System** built with **Java (Spring Boot)**, **MySQL/H2**, and a **vanilla JavaScript/HTML5** frontend. It manages inventory, products, customers, billing, and order processing through secure, role-based REST APIs.

Built as a portfolio/resume project demonstrating full-stack development: REST API design, JWT authentication, relational data modeling, CRUD operations, and a responsive frontend consuming those APIs.

## 🏗️ Architecture & Technology Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 17 |
| **Backend** | Spring Boot 3.2.5, Spring MVC, Spring Data JPA, Spring Security |
| **Auth** | JWT (jjwt) |
| **Database** | MySQL (production), H2 (in-memory demo) |
| **Frontend** | HTML5, CSS3, Vanilla JavaScript (fetch API) |
| **Build tool** | Maven |
| **Testing** | JUnit 5, Mockito |
| **Other** | Lombok, Jakarta Bean Validation |

## 📂 Project Structure

`grocery-management-system/`
- `pom.xml` | Maven configuration with Spring Boot, JPA, Security, JWT dependencies
- `src/`
  - `main/`
    - `java/com/grocery/management/`
      - `config/` | Security, JWT filter/util, CORS
      - `controller/` | REST Controllers (Auth, Category, Product, Customer, Order, Dashboard)
      - `dto/` | Request/Response DTOs
      - `entity/` | JPA Entities (User, Category, Product, Customer, Order)
      - `repository/` | Spring Data JPA Repositories
      - `service/` | Business Logic Layer
      - `GroceryManagementApplication.java`
    - `resources/`
      - `application.properties` | H2 & MySQL config, seeded demo data
      - `application-h2.properties`
      - `static/` | Frontend (HTML/CSS/JS)
      - `css/style.css` | Design system with CSS variables
  - `test/java/` | Test classes
- `docs/` | Postman collection

## 🔐 Authentication & Authorization

### JWT-Based Security
- **Login/Register** endpoints: `POST /api/auth/login`, `POST /api/auth/register`
- **Two roles**: `ADMIN` (full access), `STAFF` (customers & orders, read-only catalog)
- **JWT stored** in `localStorage`, attached via `Authorization: Bearer <token>` header
- **Custom UserDetailsService** loads user from DB, maps roles to GrantedAuthorities
- **JwtAuthFilter** intercepts every request, validates token, sets Spring Security context

### Public Endpoints
- `/api/auth/**`, `/`, `/index.html`, `/login.html`, `/css/**`, `/js/`, `/favicon.ico`

### Secured Endpoints
All other API endpoints require valid JWT token with appropriate role.

## 📦 Entity Relationships

```
User (1) ──< Order (1) ──< OrderItem (1) ──< Product
User (1) ──< Customer
Category (1) ──< Product (N)
```

### Key Entities

#### User
- `id`, `username`, `password`, `email`, `role` (ADMIN/STAFF)
- Implements `UserDetails` for Spring Security

#### Category
- `id`, `name` (unique, case-insensitive)
- `createdAt`, `updatedAt`

#### Product
- `id`, `name`, `price`, `quantity` (stock)
- `unit` (kg/packet/pcs)
- `reorderLevel` (low-stock threshold)
- `category` (Many-To-One)
- `isLowStock` (calculated: quantity <= reorderLevel)

#### Customer
- `id`, `name`, `phone`, `email`, `address`
- Used for billing orders

#### Order
- `id`, `orderDate`, `totalAmount`, `status` (PENDING/COMPLETED/CANCELLED)
- `customer` (Many-To-One)
- `orderItems` (One-To-Many)
- **On creation**: automatically deducts stock, calculates totals

#### OrderItem
- `id`, `quantity`, `unitPrice`, `subTotal`
- `order` (Many-To-One)
- `product` (Many-To-One)

## 🌐 REST API Endpoints

### Authentication
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/auth/register` | Public | Register new user (ADMIN/STAFF) |
| POST | `/api/auth/login` | Public | Login, receive JWT token |

### Categories
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/categories` | Auth | List all categories |
| GET | `/api/categories/{id}` | Auth | Get category by ID |
| POST | `/api/categories` | Admin | Create category |
| PUT | `/api/categories/{id}` | Admin | Update category |
| DELETE | `/api/categories/{id}` | Admin | Delete category |

### Products
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/products` | Auth | List products (with optional search) |
| GET | `/api/products/{id}` | Auth | Get product by ID |
| GET | `/api/products/low-stock` | Auth | Products at or below reorder level |
| GET | `/api/products/with-suggestions` | Auth | Products with AI reorder suggestions |
| POST | `/api/products` | Admin | Create product |
| PUT | `/api/products/{id}` | Admin | Update product |
| DELETE | `/api/products/{id}` | Admin | Delete product |

### Customers
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/customers` | Auth | List customers |
| GET | `/api/customers/{id}` | Auth | Get customer by ID |
| POST | `/api/customers` | Auth | Create customer |
| PUT | `/api/customers/{id}` | Auth | Update customer |
| DELETE | `/api/customers/{id}` | Auth | Delete customer |

### Orders & Billing
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/orders` | Auth | List all orders (newest first) |
| GET | `/api/orders/{id}` | Auth | Get order with items |
| GET | `/api/orders/customer/{id}` | Auth | Orders for specific customer |
| POST | `/api/orders` | Auth | Create order (deducts stock automatically) |
| PUT | `/api/orders/{id}/status` | Auth | Update status (PENDING/COMPLETED/CANCELLED) |
| GET | `/api/orders/{id}/invoice` | Auth | Printable invoice |

### Dashboard
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/dashboard/summary` | Auth | Totals: products, customers, orders, revenue |

## 🖥️ Frontend Screens (5 Pages)

All served from Spring Boot `static/` directory at `http://localhost:8080`

### 1. login.html
- Login & Registration tabs
- Demo credentials: `admin/admin123` (Admin), `staff/staff123` (Staff)
- JWT stored in `localStorage`, auto-redirect to dashboard on login

### 2. dashboard.html
- **Stats cards**: Total products, low-stock items, customers, orders, pending orders, revenue
- **Low-stock table**: Products at or below reorder level with details
- **Sidebar**: Navigation + user info + role badge
- **Responsive**: Sidebar hides on <860px screens

### 3. products.html
- **Product table**: Name, Category, Price, Stock, Status
- **Search** and **Low-stock filter**
- **+ Add product** modal with full CRUD
- **AI reorder suggestions** (new feature)
- Product modal includes: name, category, unit, price, quantity, reorder level

### 4. customers.html
- **Customer table**: Name, Phone, Email, Address
- **Search** and **Add customer** modal
- Used for selecting customers when creating orders

### 5. orders.html
- **Order table**: Order #, Customer, Date, Total, Status
- **+ New order** modal with item builder
- **Create order**: Select customer, add items (product + quantity), auto-deduce stock
- **Print invoice** functionality
- Order items: product selection, quantity input, subtotal calculation

## 🧮 Key Business Logic

### Order Creation
1. Customer selected + items added (product + quantity)
2. **Subtotal calculated** per item: `quantity × unitPrice`
3. **Order total** = sum of all subtotals
4. **Stock deduction**: `product.quantity -= orderItem.quantity`
5. **Low-stock check**: if `product.quantity < product.reorderLevel`, product marked low-stock
6. **Order saved** with `PENDING` status, JWT-authenticated user as creator

### Order Cancellation
- Returns all items to stock
- Order status changes to `CANCELLED`

### Low-Stock Alerts
- Dashboard shows products where `quantity <= reorderLevel`
- Calculated dynamically from DB

## ⚡ AI/ML Feature: Demand Forecasting

### New Service: `DemandForecastingService`
- **`predictDemand(Long productId)`**: Predicts 30-day demand using weighted moving average
  - Analyzes last 90 days of order history
  - Recent orders weighted more heavily (formula: `quantity × (30 - daysAgo)`)
  - Returns predicted daily demand + 30-day total + confidence level (High/Medium/Low/Very Low)

- **`suggestReorderLevel(Long productId)`**: Calculates optimal reorder level
  - Predicted daily demand × 5 days safety stock
  - Returns `max(currentReorderLevel, suggestedLevel)`

- **Integrated into**:
  - `ProductService.getWithReorderSuggestions()` - shows AI suggestions in product API
  - `DashboardService.getProductForecast()` - infrastructure for dashboard card

### How It Works
1. Fetches orders for product from last 90 days
2. Calculates weighted average (recent orders matter more)
3. Projects 30-day demand
4. Suggests reorder level with 5-day safety buffer
5. Confidence based on data points: ≥60 = High, ≥30 = Medium, >0 = Low, 0 = Very Low

## 🎨 CSS Design System

The `style.css` uses **CSS Variables** for consistent theming:

```css
:root {
  --bg: #F6F4EE;        --surface: #FFFFFF;
  --ink: #1F2A22;       --ink-soft: #52604F;
  --primary: #2F5233;   --primary-dark: #1E3A22;
  --primary-soft: #E4EBE1;
  --accent: #C97B2E;    --accent-soft: #F5E4CE;
  --danger: #B23A2E;    --danger-soft: #F6E1DD;
  --border: #E3DFD3;    --radius: 10px;
  --shadow: 0 1px 2px rgba(31,42,34,0.06), 0 4px 14px rgba(31,42,34,0.06);
}
```

### Consistent Components
- **`.auth-card`**: Login/register page card
- **`.stat-card`**: Dashboard statistic cards (warn state when low-stock products exist)
- **`.sidebar`**: Fixed navigation sidebar
- **`.empty-state`**: Graphics + text for empty tables
- **`.modal`**: Product/customer/order modals
- **`.toast`**: Notification messages
- **`.stamp`**: GM logo badge (rotated -8deg)
- **`.price`, `.qty`**: IBM Plex Mono font for numeric values

**Responsive**: At `<860px`, sidebar hides, main content expands full-width.

## 🗄️ Database Configuration

### application.properties
```properties
# Spring Boot & JPA
spring.application.name=grocery-management-system
spring.main.banner-mode=off

# Datasource (MySQL prod / H2 demo)
spring.datasource.url=jdbc:mysql://localhost:3306/grocery?useSSL=FALSE&allowPublicKeyRetrieval=TRUE
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# H2 Console (for demo mode)
spring.h2.console.enabled=true

# Security
spring.security.user.name=admin
spring.security.user.password=admin123

# JWT
jjwt.generate-signature=true
```

### application-h2.properties (for demo mode)
```properties
spring.datasource.url=jdbc:h2:mem:grocery
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

### Demo Data Seeder (runs on first start with H2)
- **Admin**: `admin` / `admin123` (full access)
- **Staff**: `staff` / `staff123` (customers & orders)
- **Sample categories**: Produce, Dairy, Grains, Beverages, Meat
- **Sample products**: Basmati Rice, Wheat Flour, Milk, Eggs, etc.
- **Sample customers**: Priya Sharma, Rajesh Kumar, etc.

## 🧪 Testing

Run: `mvn test`

### Existing Tests
- **ProductServiceTest**: JUnit 5 + Mockito
  - Tests service layer with mocked repositories
  - Verifies save calls, exception handling for not-found/invalid input
  - Covers low-stock product retrieval

### Test Coverage Areas
- Service layer business logic
- Repository method functionality
- Exception handling (ResourceNotFound, BadRequest)
- JWT authentication flow
- Order creation with stock deduction

## 🚀 Running the Project

### Prerequisites
- Java 17 installed
- Maven 3.6+
- MySQL (optional, H2 works out-of-the-box)

### Commands
```bash
# Clone & navigate
git clone <repo-url>
cd grocery-management-system

# Run with H2 (demo mode - no MySQL needed)
mvn spring-boot:run

# Or build JAR first
mvn clean package
java -jar target/grocery-management-system-1.0.0.jar

# Access
# http://localhost:8080
# H2 Console: http://localhost:8080/h2-console (use jdbc:h2:mem:grocery)
```

### Demo Logins
| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Staff | staff | staff123 |
```

## 📚 API Documentation

### Postman Collection
- Located at: `docs/Grocery-Management-System.postman_collection.json`
- Covers **every endpoint** with example requests
- Includes auth flow, product CRUD, order creation, customer management

### Example Requests

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Create Order:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"items":[{"productId":1,"quantity":3},{"productId":3,"quantity":2}]}'
```

## ⚡ AI/ML Feature: Demand Forecasting

### New Service: `DemandForecastingService`
- **`predictDemand(Long productId)`**: Predicts 30-day demand using weighted moving average
  - Analyzes last 90 days of order history
  - Recent orders weighted more heavily (formula: `quantity × (30 - daysAgo)`)
  - Returns predicted daily demand + 30-day total + confidence level (High/Medium/Low/Very Low)

- **`suggestReorderLevel(Long productId)`**: Calculates optimal reorder level
  - Predicted daily demand × 5 days safety stock
  - Returns `max(currentReorderLevel, suggestedLevel)`

- **Integrated into**:
  - `ProductService.getWithReorderSuggestions()` - shows AI suggestions in product API
  - `DashboardService.getProductForecast()` - infrastructure for dashboard card

### How It Works
1. Fetches orders for product from last 90 days
2. Calculates weighted average (recent orders matter more)
3. Projects 30-day demand
4. Suggests reorder level with 5-day safety buffer
5. Confidence based on data points: ≥60 = High, ≥30 = Medium, >0 = Low, 0 = Very Low

## 🎨 CSS Design System

The `style.css` uses **CSS Variables** for consistent theming:

```css
:root {
  --bg: #F6F4EE;        --surface: #FFFFFF;
  --ink: #1F2A22;       --ink-soft: #52604F;
  --primary: #2F5233;   --primary-dark: #1E3A22;
  --primary-soft: #E4EBE1;
  --accent: #C97B2E;    --accent-soft: #F5E4CE;
  --danger: #B23A2E;    --danger-soft: #F6E1DD;
  --border: #E3DFD3;    --radius: 10px;
  --shadow: 0 1px 2px rgba(31,42,34,0.06), 0 4px 14px rgba(31,42,34,0.06);
}
```

### Consistent Components
- **`.auth-card`**: Login/register page card
- **`.stat-card`**: Dashboard statistic cards (warn state when low-stock products exist)
- **`.sidebar`**: Fixed navigation sidebar
- **`.empty-state`**: Graphics + text for empty tables
- **`.modal`**: Product/customer/order modals
- **`.toast`**: Notification messages
- **`.stamp`**: GM logo badge (rotated -8deg)
- **`.price`, `.qty`**: IBM Plex Mono font for numeric values

**Responsive**: At `<860px`, sidebar hides, main content expands full-width.

## 🗄️ Database Configuration

### application.properties
```properties
# Spring Boot & JPA
spring.application.name=grocery-management-system
spring.main.banner-mode=off

# Datasource (MySQL prod / H2 demo)
spring.datasource.url=jdbc:mysql://localhost:3306/grocery?useSSL=FALSE&allowPublicKeyRetrieval=TRUE
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# H2 Console (for demo mode)
spring.h2.console.enabled=true

# Security
spring.security.user.name=admin
spring.security.user.password=admin123

# JWT
jjwt.generate-signature=true
```

### application-h2.properties (for demo mode)
```properties
spring.datasource.url=jdbc:h2:mem:grocery
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

### Demo Data Seeder (runs on first start with H2)
- **Admin**: `admin` / `admin123` (full access)
- **Staff**: `staff` / `staff123` (customers & orders)
- **Sample categories**: Produce, Dairy, Grains, Beverages, Meat
- **Sample products**: Basmati Rice, Wheat Flour, Milk, Eggs, etc.
- **Sample customers**: Priya Sharma, Rajesh Kumar, etc.

## 🧪 Testing

Run: `mvn test`

### Existing Tests
- **ProductServiceTest**: JUnit 5 + Mockito
  - Tests service layer with mocked repositories
  - Verifies save calls, exception handling for not-found/invalid input
  - Covers low-stock product retrieval

### Test Coverage Areas
- Service layer business logic
- Repository method functionality
- Exception handling (ResourceNotFound, BadRequest)
- JWT authentication flow
- Order creation with stock deduction

## 🚀 Running the Project

### Prerequisites
- Java 17 installed
- Maven 3.6+
- MySQL (optional, H2 works out-of-the-box)

### Commands
```bash
# Clone & navigate
git clone <repo-url>
cd grocery-management-system

# Run with H2 (demo mode - no MySQL needed)
mvn spring-boot:run

# Or build JAR first
mvn clean package
java -jar target/grocery-management-system-1.0.0.jar

# Access
# http://localhost:8080
# H2 Console: http://localhost:8080/h2-console (use jdbc:h2:mem:grocery)
```

### Demo Logins
| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Staff | staff | staff123 |
```

## 📚 API Documentation

### Postman Collection
- Located at: `docs/Grocery-Management-System.postman_collection.json`
- Covers **every endpoint** with example requests
- Includes auth flow, product CRUD, order creation, customer management

### Example Requests

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Create Order:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"items":[{"productId":1,"quantity":3},{"productId":3,"quantity":2}]}'
```

**Get Low Stock:**
```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/products/low-stock
```
**Get Low Stock:**
```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/products/low-stock
## 📋 HR Project Interview - Q&A Section

### Section 1: Project Introduction

#### Q1: "Tell me about your Grocery Management System project."
**A1:** "My Grocery Management System is a full-stack Java Spring Boot application that manages grocery store operations. It handles inventory, customers, and orders through a secure web interface. The backend uses Spring Boot with REST APIs, Spring Security with JWT authentication, and MySQL/H2 database. The frontend is built with vanilla HTML/CSS/JavaScript that consumes the APIs. Key features include product CRUD operations, customer management, order processing with automatic stock deduction, and a dashboard with sales analytics. I designed the system to replace manual spreadsheet tracking, reducing errors and saving time for store owners. The project demonstrates my ability to build complete full-stack applications from database design to user interface."

#### Q2: "What inspired you to build this project?"
**A2:** "I wanted to solve a real problem that small grocery store owners face. Many still use pen-and-paper or basic spreadsheets for inventory and billing, which is error-prone and time-consuming. I wanted to create a solution that's affordable, easy to use, and runs on a single system (no separate frontend server needed). The H2 in-memory demo mode was specifically added so the app runs immediately without installing MySQL, making it accessible for portfolio review. I also wanted to add modern features like AI demand forecasting to show I'm keeping up with industry trends."

#### Q3: "What's the biggest feature you're most proud of?"
**A3:** "I'm most proud of the AI-powered demand forecasting feature I added. While the core CRUD functionality was a good start, I wanted to add something that demonstrates advanced Java skills. I implemented a weighted moving average algorithm that analyzes 90 days of order history to predict 30-day demand and suggest optimal reorder levels. This feature alone shows I can combine core Java skills (Collections, statistical analysis) with practical business value. It's been the talking point in every interview I've had."
```

## 📋 HR Project Interview - Q&A Section

### Section 1: Project Introduction

#### Q1: "Tell me about your Grocery Management System project."
**A1:** "My Grocery Management System is a full-stack Java Spring Boot application that manages grocery store operations. It handles inventory, customers, and orders through a secure web interface. The backend uses Spring Boot with REST APIs, Spring Security with JWT authentication, and MySQL/H2 database. The frontend is built with vanilla HTML/CSS/JavaScript that consumes the APIs. Key features include product CRUD operations, customer management, order processing with automatic stock deduction, and a dashboard with sales analytics. I designed the system to replace manual spreadsheet tracking, reducing errors and saving time for store owners. The project demonstrates my ability to build complete full-stack applications from database design to user interface."

#### Q2: "What inspired you to build this project?"
**A2:** "I wanted to solve a real problem that small grocery store owners face. Many still use pen-and-paper or basic spreadsheets for inventory and billing, which is error-prone and time-consuming. I wanted to create a solution that's affordable, easy to use, and runs on a single system (no separate frontend server needed). The H2 in-memory demo mode was specifically added so the app runs immediately without installing MySQL, making it accessible for portfolio review. I also wanted to add modern features like AI demand forecasting to show I'm keeping up with industry trends."

#### Q3: "What's the biggest feature you're most proud of?"
**A3:** "I'm most proud of the AI-powered demand forecasting feature I added. While the core CRUD functionality was a good start, I wanted to add something that demonstrates advanced Java skills. I implemented a weighted moving average algorithm that analyzes 90 days of order history to predict 30-day demand and suggest optimal reorder levels. This feature alone shows I can combine core Java skills (Collections, statistical analysis) with practical business value. It's been the talking point in every interview I've had."

### Section 2: Technology & Architecture

#### Q4: "Why did you choose Spring Boot for the backend?"
**A4:** "Spring Boot was the natural choice because it eliminates the boilerplate configuration that plain Spring requires. I wanted to focus on business logic rather than setup. The embedded Tomcat server means the app runs with `mvn spring-boot:run` without needing an external application server. Spring Data JPA automates 90% of database operations, so I can focus on the grocery-specific logic. The ecosystem is mature, with excellent documentation, and it's the industry standard for enterprise Java applications."

### Section 3: Database & Data Flow

#### Q8: "Walk me through the database schema."
**A8:** "The system has 6 main tables: users, categories, products, customers, orders, and order_items. Users have a one-to-many relationship with orders. Categories have a one-to-many relationship with products. Customers have a one-to-many relationship with orders. Orders have a one-to-many relationship with order_items. Products appear in many order_items. The key foreign keys are: products.category_id references categories.id, orders.customer_id references customers.id, and order_items references both orders and products. I used JPA annotations (@Entity, @Table, @Id, @Column, @OneToMany, @ManyToOne) to map these relationships. The H2 database uses the same schema, so the transition to production MySQL is seamless."

#### Q9: "How does order creation affect the database?"
**A9:** "When an order is created, three things happen atomically: 1) A new order record is created with PENDING status and the total calculated from item subtotals. 2) Each order_item record is created linking the product, quantity, unit price, and subtotal. 3) Product stock is deducted - the product's quantity field is reduced by the ordered quantity. If any product doesn't have sufficient stock, the entire operation rolls back. This ensures we never sell more than we have in stock. The order total is the sum of all item subtotals (quantity × unit_price)."

#### Q10: "Explain the low-stock alert mechanism."
**A10:** "The system checks for low-stock products by running a query: `SELECT * FROM products WHERE quantity <= reorderLevel`. This runs every time the dashboard loads. Products meeting this criteria are flagged with `isLowStock = true`. On the dashboard, I display these in a dedicated table with the product name, category, current quantity, and reorder level. The stat card turns orange (using the `--accent` CSS variable) when there are low-stock items. This helps store owners know what to reorder before running out of stock."

#### Q11: "How does the H2 demo mode work without MySQL?"
**A11:** "I configured two sets of properties files: `application.properties` for MySQL production and `application-h2.properties` for the demo mode. When the Spring profile is set to 'h2', the application uses an in-memory database `jdbc:h2:mem:grocery` that loses data on shutdown but is perfect for demonstrations. The `ddl-auto=create-drop` setting automatically creates tables on startup and drops them on shutdown. Demo data (admin user, sample categories, products, customers) is seeded via an SQL script that runs on application startup. This means I can say 'run the app' and it works immediately, which is great for interviews and presentations."
#### Q5: "Why did you use JWT for authentication instead of sessions?"
**A5:** "JWT (JSON Web Tokens) was chosen for several reasons: First, it's stateless - the server doesn't need to store session data, which scales better. Second, the token contains user role information, so I don't need to query the database for every request to check permissions. Third, it's the standard for modern REST APIs and single-page applications. The user token is stored in browser localStorage and attached to every API call via an interceptor. The downside is that logout requires manual token clearance, but I handled this in the frontend JavaScript."

#### Q6: "Explain the role-based access control you implemented."
**A6:** "I implemented two roles: ADMIN and STAFF. ADMIN has full access - all CRUD operations on all entities. STAFF can manage customers and process orders, but has read-only access to the product catalog. I configured this in Spring Security's `configure(HttpSecurity http)` method. Public endpoints are `/api/auth/**` and `/`, `/login.html`, `/css/**`, `/js/`. All other routes require authentication. I used `@PreAuthorize('hasRole('ADMIN')')` annotations on controller methods for method-level security. The UI also respects roles - the sidebar nav links and available actions change based on user role."

#### Q7: "How does the CSS design system work?"
### Section 4: Features & Implementation

#### Q12: "How does the product search feature work?"
**A12:** "The product search uses the `search()` method in ProductService that calls `productRepository.findByNameContainingIgnoreCase(name)`. This generates a SQL `SELECT * FROM products WHERE name LIKE '%searchTerm%'` query, case-insensitive. The frontend sends the search term as a query parameter `/api/products?search=rice`, and the backend filters the results. This is efficient for small-to-medium product catalogs. For very large databases, I'd implement pagination and full-text search, but for a portfolio project, this approach is sufficient and demonstrates JPA query methods."

#### Q13: "What was the most challenging feature to implement?"
**A13:** "The order creation with automatic stock deduction was the most challenging. I had to ensure that: 1) Stock is only deducted if there's sufficient quantity, 2) If an order is cancelled, stock is returned, 3) The order total calculates correctly including all items, 4) Low-stock flags update appropriately. I used Spring's `@Transactional` annotation to ensure atomicity - either all database changes succeed or none do. I also had to handle the edge case where a product goes from above reorder level to below during order creation, which triggers the low-stock alert on the next dashboard load."

#### Q14: "How did you handle order cancellation and restocking?"
**A14:** "When an order is cancelled, the `updateStatus()` method in OrderService changes the order status to CANCELLED and iterates through the order items. For each item, it retrieves the product and increases the quantity by the ordered amount: `product.setQuantity(product.getQuantity() + item.getQuantity())`. This restocks all items from the cancelled order. Then I save the product and the order. I also added a check: if the restored stock brings the product above the reorder level, the low-stock flag should clear on the next dashboard load. This required coordination between the order service and dashboard service."

#### Q15: "Describe the customer flow when creating an order."
**A15:** "When creating an order, the user first selects a customer from a dropdown list populated by `customerService.getAll()`. The selected customer's ID is stored and included in the order API request. During order item addition, the customer name displays at the top of the order modal for confirmation. After order completion, the system can filter orders by customer ID to show a customer's order history. This is useful for repeat business analysis and targeted marketing. The customer data (name, phone, email) also appears on the printed invoice."
**A7:** "I used CSS custom properties (variables) in the `:root` selector to define a consistent color palette: `--bg` for background, `--surface` for card backgrounds, `--primary` for main accents, `--accent` for highlights, `--danger` for warnings, `--ink` for text, and `--border` for element borders. Each variable has a light and soft/danger variant. This means changing one line in the CSS updates the entire application's appearance. I also used `rem` units for scalability and `box-sizing: border-box` for predictable layouts. The design is responsive - at screens below 860px, the sidebar hides and the main content expands to full width."
### Section 6: Developer Ownership

#### Q21: "Walk me through what you built from scratch."
**A21:** "I built the entire application from the ground up. This includes: the full Spring Boot application structure with proper package organization; 6 JPA entities with correct relationship mappings and annotations; 6 Spring Data JPA repositories with custom queries for business needs; 7 REST controllers covering all API endpoints with proper HTTP status codes; the security configuration with JWT filter, user details service, and role-based access; the service layer with business logic for orders, products, customers, dashboard, and the new AI forecasting feature; 5 HTML screens with consistent CSS styling and responsive design; the JavaScript helpers for API calls, auth state, sidebar rendering, and toast notifications; the database seeding for demo mode with default admin/staff accounts and sample data; and the AI demand forecasting algorithm with weighted moving average and reorder level suggestions."
### Section 5: Challenges & Problem-Solving

#### Q16: "What was the most difficult bug you faced, and how did you fix it?"
**A16:** "The most difficult bug was the CORS (Cross-Origin Resource Sharing) issue. During development, the frontend (running on localhost or served from files) was making API calls to the backend, and the browser blocked them due to security policies. The error was 'Blocked by CORS: No 'Access-Control-Allow-Origin' header is present on the requested resource'. I fixed this by configuring the SecurityConfig.java to permit certain origins and methods: `CorsConfigurationSource` bean that allows origins from the frontend URL and methods GET, POST, PUT, DELETE. I also had to ensure the frontend's `fetch()` wrapper adds the `Authorization` header correctly with the JWT token. This is a common full-stack issue and fixing it demonstrated my understanding of both frontend and backend security."

#### Q17: "How did you handle validation errors?"
**A17:** "I used Jakarta Bean Validation annotations on my DTO classes: `@NotNull`, `@Size`, `@Email`, `@Valid`. When invalid data is submitted, Spring automatically returns a 400 Bad Request with a JSON error response detailing which fields are invalid. I also created a global exception handler (`GlobalExceptionHandler`) that catches `MethodArgumentNotValidException` and returns a consistent error format with `success: false`, the error message, and a timestamp. This prevents raw Java exceptions from appearing in the API response, which is important for production security and user experience."

#### Q18: "What approach did you take for the AI demand forecasting feature?"
**A18:** "I wanted the AI feature to be 100% matched to the project's core purpose (inventory management), not just adding machine learning for show. I implemented a weighted moving average algorithm that analyzes the last 90 days of order history. Recent orders are weighted more heavily (formula: quantity × (30 - daysAgo)). This predicts 30-day demand and suggests a reorder level with 5-day safety stock. I calculated confidence based on data points: High (≥60 orders), Medium (≥30 orders), Low (>0 orders), Very Low (no history). The feature is integrated into the ProductService's `getWithReorderSuggestions()` method, so when viewing products, the AI suggests whether the current reorder level should be adjusted. It's practical, not just decorative."

#### Q19: "How did you ensure the UI is consistent across all screens?"
**A19:** "I designed the UI with a consistent CSS variable system from the start. All five HTML screens (login, dashboard, products, customers, orders) share the same `style.css` file with identical variable definitions. I used the same component patterns: `.auth-card` for login/register, `.stat-card` for dashboard metrics, `.sidebar` for navigation, `.empty-state` for tables with no data, `.modal` for forms, and `.toast` for notifications. The `renderSidebar()` function in `common.js` builds the sidebar navigation on every authenticated page, ensuring the same links appear in the same order. I also used consistent class names and avoided inline styles, which makes global changes easier."

#### Q20: "If you were to add new features, what would they be?"
**A20:** "I'd add pagination and sorting to the list endpoints for larger datasets. I'd implement refresh tokens for better session management and a token blacklist on logout. Supplier management and purchase order functionality would complement the existing product system. Sales reports with daily/monthly revenue charts would enhance the dashboard analytics. I'd also add Docker containerization and a Dockerfile for easy deployment. These are natural extensions that build on the existing architecture without requiring a complete redesign."
### Section 7: Preparation Tips

#### Q25: "How should I prepare for an interview using this project?"
**A25:** "I recommend: running the project with `mvn spring-boot:run` and testing all features; logging in as both admin and staff to see role differences; creating an order, cancelling it, and checking stock restoration; testing the low-stock alerts by setting product quantities low; reviewing the AI reorder suggestions on product details; reading through the Postman collection for API endpoints; and being prepared to explain design decisions, not just code. I should be able to discuss why I chose each technology, what challenges I faced, and how I solved them."

#### Q26: "What technical keywords should I master?"
**A26:** "Key terms include: Spring Boot, Spring Security, JWT, JPA/Hibernate, MySQL/H2; REST APIs, MVC pattern, MVC architecture; CSS variables, responsive design, vanilla JavaScript; weighted moving average, demand forecasting, safety stock; Maven, Git, Lombok, Jakarta Validation."

#### Q27: "What are the most important talking points?"
**A27:** "My key talking points are: full-stack capability (building from database to UI independently); production-ready mindset (error handling, validation, security, transactions); AI/ML integration (demand forecasting for business value); user-centered design (consistent UI, demo mode accessibility, role-based UX); and problem-solving (debugging, transaction management, data consistency)."

#### Q28: "Who is the target audience for this project?"
**A28:** "The project targets small grocery store owners who need to replace manual tracking with an automated system; job seekers demonstrating full-stack Java skills to employers; and portfolio reviewers who want to see a complete, functional application with modern features like AI forecasting."

#### Q29: "How does this project compare to tutorial-based projects?"
**A29:** "Unlike tutorial-based projects where every feature is explained step-by-step, this project was designed from scratch to solve a real problem. I made independent technology choices, designed the database schema, implemented the AI feature from research, and created the UI with consistent styling. The demo mode setup shows consideration for the user's experience, and the AI forecasting demonstrates I'm keeping up with industry trends rather than just following a guide."

#### Q30: "What's the future roadmap for this project?"
**A30:** "Future enhancements would include pagination and sorting for larger datasets; refresh tokens and token blacklist for improved session management; supplier management and purchase order functionality to complement the existing product system; sales reports with daily/monthly revenue charts for enhanced dashboard analytics; Docker containerization and a Dockerfile for easy deployment; and role-based UI customization where Admins can configure which features staff members can access."