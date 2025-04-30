# PrismWeather

PrismWeather is a Spring Boot-based web application that provides weather forecasts, user profile management, and location-based weather alerts. It integrates with multiple weather APIs (OpenWeather and WeatherAPI.com) to fetch real-time weather data and supports user authentication and authorization using JWT.

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

  - Fetch daily weather forecasts for specific locations with min/max temperatures and humidity.
  - Retrieve current weather conditions including temperature, wind, and cloud data.
  - Intelligent aggregation of 3-hourly forecast data into daily summaries.

- **Location Management**:

  - Add, view, and delete user-specific locations.
  - Geocoding service to convert location names to geographic coordinates.
  - Secure access control ensuring users can only access their own locations.

- **Weather Alerts**:

  - Get weather alerts for specific locations with detailed event information.
  - Alert data includes event type, sender, effective/expiry times, and descriptions.
  - Integration with WeatherAPI.com for reliable alert information.

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

  - OpenWeather API (Current weather, forecasts, and geocoding)
  - WeatherAPI.com (Weather alerts)

- **Other Libraries**:
  - Lombok (Boilerplate code reduction)
  - SpringDoc (Swagger/OpenAPI integration)
  - Rate limiting for API calls
  - Caching with Redis to improve performance

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
   git clone https://github.com/m3h3d1/PrismWeather.git
   cd PrismWeather
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

## Project Structure

The project follows a standard Spring Boot application structure:

```
src/main/java/com/mehedi/prismweather/
├── config/           # Configuration classes (Security, Redis, etc.)
├── controller/       # REST API controllers
├── dto/              # Data Transfer Objects
│   ├── alerts/       # Weather alert DTOs
│   ├── auth/         # Authentication DTOs
│   ├── location/     # Location DTOs
│   └── weather/      # Weather and forecast DTOs
├── exception/        # Custom exceptions and error handling
├── model/            # JPA entity classes
├── repository/       # Spring Data JPA repositories
├── security/         # Security-related classes (JWT, etc.)
├── service/          # Business logic services
└── util/             # Utility classes
```

---

## Configuration

The application requires several configuration parameters in the `application.yml` file:

### Database Configuration
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/prismweather
    username: your_username
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update  # Use 'create' for first run, then 'update'
```

### Security Configuration
```yaml
security:
  jwt:
    secret: your_jwt_secret_key
    expiration-ms: 86400000  # 24 hours
```

### API Keys
```yaml
openweather:
  api:
    key: your_openweather_api_key

weatherapi:
  key: your_weatherapi_key
```

### Redis Configuration (if using Redis)
Configure Redis connection details in the RedisConfig class.

### Caching Configuration
The application uses Redis for caching to improve performance and reduce the number of external API calls. Caching is implemented using Spring's caching annotations.

The following caches are configured:
- **weather**: Caches current weather data by location ID
- **forecast**: Caches weather forecast data by location ID
- **geocoding**: Caches geocoding results by location name

Cache entries are automatically invalidated when the data is updated or after a configured time-to-live period.

### Rate Limiting Configuration
The application implements rate limiting to prevent abuse of external APIs and ensure fair usage. Rate limiting is configured as follows:

- **Maximum Requests**: 3 requests per time window
- **Time Window**: 10 seconds
- **Implementation**: Redis-based token bucket algorithm

Rate limiting is applied to the following API endpoints:
- Weather API calls (current weather and forecasts)
- Geocoding API calls
- Weather Alert API calls

When a rate limit is exceeded, the API returns a 429 Too Many Requests status code with information about when the client can retry.

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
