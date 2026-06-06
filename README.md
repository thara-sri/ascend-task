### Project Status:
This repository is part of a Back-End assessment task.

You can find the implementation details and the full source code in the `feature/book-management-api` branch.

Please review the work via this Pull Request: https://github.com/thara-sri/ascend-task/pull/1

---

# Book Management API

A RESTful API built with Java and Spring Boot for managing a collection of books. This project uses MySQL as the primary database and implements optimized queries to handle large datasets efficiently.

---

## 1. Prerequisites

Before running this application, please ensure you have the following installed on your machine:
* **Docker** and **Docker Compose** (The easiest way to run both the application and the database).
* *Optional:* Java 17 and Maven (If you wish to run the application locally without Docker).

---

## 2. Steps to Set Up the Database & Run the Server

Thanks to containerization, setting up the database and running the server is entirely automated.

### Running with Docker Compose (Recommended)
Simply navigate to the root directory of the project and execute the following command:

```bash
docker-compose up -d
```

### What happens behind the scenes?

1. The `db` container will spin up a MySQL instance and automatically create a database named `my_database`.

2. The `app` container will wait until the database is healthy, then build and start the Spring Boot application on port `8080`.

3. Hibernate (`ddl-auto=update`) will automatically connect to the database and generate the required tables and indexes upon startup.

### Database Schema Script (For Reference)
Although the application utilizes Spring Data JPA and Hibernate to automatically generate the schema, below is the exact SQL script representing the generated database structure, fulfilling the project requirements:

```sql
-- 1. Create the database (Automated by Docker)
CREATE DATABASE IF NOT EXISTS my_database;
USE my_database;

-- 2. Create the 'book' table
CREATE TABLE IF NOT EXISTS book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    published_date DATE,
    
    -- 3. Create an index on the 'author' column to prevent full table scans
    -- This optimizes the performance of the GET /books?author={authorName} endpoint
    INDEX idx_book_author (author)
);
```
**Technical Note on Optimization:** The requirement stated to avoid full table scans when fetching books by a specific author. To solve this, a B-Tree index (`idx_book_author`) is explicitly created on the `author` column. This allows the database to perform high speed logarithmic lookups ($$O(\log n)$$) instead of linear table scans ($$O(n)$$) when the `GET /books?author={authorName}` endpoint is called.

---

## 3. Running the Integration Tests

This project includes comprehensive integration tests to verify the functionality of all endpoints and their interactions with the MySQL database. 

### How to Execute (Via Docker)
Since the application is fully containerized, you can run the test suite directly inside the running `app` container. This ensures a consistent testing environment without needing to install Java or Maven on your local machine.

While the containers are running (`docker-compose up -d`), execute the following command in your terminal:

```bash
docker compose exec app ./mvnw test
```
### What is being tested?
- **POST /books:**

    - Verifies successful book creation.

    - Ensures correct date conversion from the Buddhist Calendar to the Christian Era (CE) before saving to the database.

    - Validates constraints (e.g., rejecting years < 1000 or future dates, throwing `400 Bad Request` for empty titles/authors).

- **GET /books?author={authorName}:**

    - Verifies successful retrieval of books matching the exact author name.

    - Ensures a 200 OK with an empty array `[]` is returned when no books are found.

- **Database Integrity:**

    - All test methods are wrapped with `@Transactional`. This ensures that any mock data inserted during the testing phase is automatically rolled back, keeping the database clean and preventing side effects between tests.

---

## 4. Example API Requests and Expected Responses

### 1. Save a Book
**Endpoint:** `POST /api/books`

**Content-Type:** `application/json`

**Request Body:**
```json
{
    "title": "Clean Architecture",
    "author": "Robert C. Martin",
    "publishedDate": "2560-10-20"
}
```
**Expected Response (201 Created):**
```json
{
    "id": 1,
    "title": "Clean Architecture",
    "author": "Robert C. Martin",
    "publishedDate": "2017-10-20"
}
```
*(Note: The Buddhist calendar year 2560 is automatically validated and converted to the CE year 2017 before being persisted to the database).*

### 2. Get Books by Author ###
**Endpoint:** `GET /api/books?author=Robert C. Martin`

**Expected Response (200 OK):**
```json
[
    {
        "id": 1,
        "title": "Clean Architecture",
        "author": "Robert C. Martin",
        "publishedDate": "2017-10-20"
    }
]
```
