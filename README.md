# PrismWeather

PrismWeather is a Spring Boot-based web application that provides weather forecasts, user profile management, and location-based weather alerts. It integrates with external APIs like OpenWeather to fetch real-time weather data and supports user authentication and authorization using JWT.

---

## Table of Contents

1. [Features](#features)
2. [Technologies Used](#technologies-used)
3. [Project Structure](#project-structure)
4. [Setup and Installation](#setup-and-installation)
5. [API Endpoints](#api-endpoints)
6. [Configuration](#configuration)
7. [Contributing](#contributing)
8. [License](#license)

---

## Features

- **User Authentication and Authorization**:

  - Secure login, registration, and logout using JWT.
  - Role-based access control (e.g., USER, ADMIN).

- **Weather Forecasts**:

  - Fetch daily weather forecasts for specific locations.
  - Retrieve current weather conditions.

- **Location Management**:

  - Add, view, and delete user-specific locations.

- **Weather Alerts**:

  - Get weather alerts for specific locations.

- **User Profile Management**:

  - Update user profiles with fields like name, email, phone number, and address.

- **Swagger API Documentation**:
  - Interactive API documentation using Swagger.

---

## Technologies Used

- **Backend**:

  - Spring Boot (Web, Security, Data JPA, Validation)
  - Hibernate (JPA implementation)
  - MySQL (Database)
  - Redis (Caching)

- **Authentication**:

  - JWT (JSON Web Tokens)

- **External APIs**:

  - OpenWeather API (Weather data)

- **Other Libraries**:
  - Lombok (Boilerplate code reduction)
  - SpringDoc (Swagger/OpenAPI integration)

---

## Project Structure

```
PrismWeather/
├── src/
│ ├── main/
│ │ ├── java/com/mehedi/prismweather/
│ │ │ ├── config/ # Configuration files (e.g., Swagger, Security, Redis)
│ │ │ ├── controller/ # REST controllers for handling API requests
│ │ │ ├── dto/ # Data Transfer Objects for requests and responses
│ │ │ ├── exception/ # Custom exception handling
│ │ │ ├── model/ # JPA entity models
│ │ │ ├── repository/ # Spring Data JPA repositories
│ │ │ ├── service/ # Business logic and service layer
│ │ │ ├── util/ # Utility classes (e.g., JWT utilities)
│ │ │ └── PrismWeatherApplication.java # Main application entry point
│ │ └── resources/
│ │ ├── application.yml # Application configuration
│ │ └── static/ # Static resources (if any)
├── build.gradle # Gradle build configuration
├── settings.gradle # Gradle settings
├── README.md # Project documentation
└── HELP.md # Additional help
```

---

## Setup and Installation

### Prerequisites

- Java 21 or higher
- Gradle
- MySQL
- Redis

### Steps

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/your-repo/prismweather.git
   cd prismweather
   ```

2. **Configure the Database**:

   - Update the `application.yml` file in `src/main/resources` with your MySQL database credentials.

3. **Run Redis**:

   - Ensure Redis is running locally or update the Redis configuration in `RedisConfig.java`.

4. **Build the Project**:
   ./gradlew build

5. **Run the Application**:
   ./gradlew bootRun

6. **Access the Application**:
   - API Documentation: http://localhost:8080/docs

---

## API Endpoints

### Authentication

- POST /api/auth/login: Login and retrieve a JWT token.
- POST /api/auth/register: Register a new user.
- POST /api/auth/logout: Logout and invalidate the JWT token.
- POST /api/auth/password/reset: Reset user password.

### User Management

- GET /api/users/current: Get the current logged-in user's profile.
- PUT /api/users/{id}: Update a user's profile.

### Location Management

- POST /api/locations: Add a new location.
- GET /api/locations: Retrieve all locations for the logged-in user.
- DELETE /api/locations/{id}: Delete a location.

### Weather

- GET /api/locations/{id}/weather: Get current weather for a location.
- GET /api/locations/{id}/forecast: Get daily weather forecast for a location.
- GET /api/locations/{id}/alerts: Get weather alerts for a location.

---
