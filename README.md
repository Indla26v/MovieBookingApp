# Movie Booking Web Application

A full-stack web application built using **Spring Boot** and **Thymeleaf** that allows users to browse and book movie tickets, while providing management interfaces for admins and theatre managers.

## Features

- **User Roles:**
  - **Admin:** Manages total operations, users, and overall system health.
  - **Manager:** Manages specific theatres, screens, and movie assignments.
  - **User:** Can browse movies, select seats, and view booking history.
- **Authentication & Security:** Secured with Spring Security, supporting user registration, authentication, and OAuth 2 integration.
- **Movie & Theatre Management:** Create and manage theatres, add movies, and handle scheduling.
- **Booking System:** Seat selection engine, booking confirmations, and booking history tracking.
- **Database:** Uses MySQL to handle persistence using Spring Data JPA.

## Tech Stack

- **Java Version:** 21
- **Framework:** Spring Boot 3.5.0
- **Frontend Template Engine:** Thymeleaf
- **Database:** MySQL
- **ORM / Persistence:** Spring Data JPA / Hibernate
- **Security:** Spring Security
- **Other Tools:** Lombok, Jakarta Validation

## Setup Instructions

1. **Clone the repository:**
   \\\ash
   git clone <repository_url>
   cd SpringMbooking
   \\\

2. **Database Setup:**
   Ensure you have MySQL running on \localhost:3306\.
   - Update your configuration inside \src/main/resources/application.properties\ with your MySQL credentials.
   The database \movie_booking\ will be created automatically.

3. **Build the Application:**
   \\\ash
   ./mvnw clean install
   \\\

4. **Run the Application:**
   \\\ash
   ./mvnw spring-boot:run
   \\\

5. **Access the Application:**
   Open your browser and navigate to \http://localhost:8081\

