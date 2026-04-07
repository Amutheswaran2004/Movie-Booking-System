# Movie Booking System

This is a backend project for a Movie Booking System, built using **Java** and **Spring Boot**. The project uses **Spring Data JPA** for database interactions and **PostgreSQL** as the relational database.

## Technologies Used
* **Java 17**
* **Spring Boot 4.0.1**
* **Spring Web MVC**
* **Spring Data JPA**
* **PostgreSQL**
* **Maven**

## Features (Backend)
* REST API endpoints for managing movie bookings.
* Database integration for persisting data such as movies, users, reservations, etc.
* Structured in a standard Spring Boot architecture.

## Getting Started

### Prerequisites
* Java 17 or higher
* Maven 3.x
* PostgreSQL server running locally or remotely

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/Amutheswaran2004/Movie-Booking-System.git
   ```
2. Navigate into the project directory:
   ```bash
   cd Movie-Booking-System/movieBookingSystem
   ```
3. Update `src/main/resources/application.properties` with your PostgreSQL database credentials.

4. Run the project using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
   Or, build the jar and run:
   ```bash
   ./mvnw clean package
   java -jar target/movieBookingSystem-0.0.1-SNAPSHOT.jar
   ```

## Contributing
Feel free to submit issues or pull requests.
