# 🐄 PashuSathi

### A Smart Dairy & Livestock Assistance Platform for Farmers

PashuSathi is a web-based platform designed to help farmers and dairy operators manage livestock and dairy-related activities.

## Technology Stack

* Java 17
* Spring Boot 3.3.5
* Spring Data JPA
* Spring Security
* MySQL 8
* Flyway
* REST APIs
* Bootstrap 5
* Swagger / OpenAPI

## Currently Implemented

* Spring Boot application setup
* MySQL database connectivity
* Database migration using Flyway
* Dairy and User foundation tables
* Public health-check REST API
* Spring Security configuration
* Swagger API documentation
* Basic frontend landing page
* Automated backend tests

## Project Structure

```text
backend/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/pashusathi/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   └── PashuSathiApplication.java
│   │   └── resources/
│   │       ├── db/migration/
│   │       └── static/
│   └── test/
└── .gitignore
```

## Running the Project

1. Install JDK 17 and MySQL 8.
2. Create the `pashusathi` database.
3. Configure the `DB_PASSWORD` environment variable.
4. Open the project in IntelliJ IDEA.
5. Run `PashuSathiApplication.java`.

Application runs at:

`http://localhost:8081` (if configured to use port 8081).

## API Documentation

Swagger UI:

`http://localhost:8081/swagger-ui.html`

Health API:

`GET /api/health`

## Project Status

Initial backend foundation completed. Farmer management and additional dairy management modules are planned for subsequent development.
