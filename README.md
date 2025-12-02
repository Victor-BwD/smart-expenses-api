# ☕ Smart Expenses API

![Java](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue) ![Flyway](https://img.shields.io/badge/Flyway-Migration-red) ![Docker](https://img.shields.io/badge/Docker-Enabled-blue)

> A robust, high-performance REST API for intelligent personal finance management, built with the latest Java standards.

## 📋 Project Overview

**Smart Expenses API** goes beyond simple expense tracking. It implements an intelligent classification engine that automatically assigns categories (e.g., Grocery, Housing, Entertainment) to expenses based on user-defined keywords and description analysis.

Built to be production-ready, this project leverages the power of **Java 21** and **Spring Boot 3**, ensuring high throughput and type safety. Database integrity is strictly managed via **Flyway** migrations, and the entire environment is containerized with **Docker**.

## 🚀 Key Features

- **🔐 Secure Authentication:** JWT (JSON Web Token) implementation for stateless and secure user registration and login.
- **🧠 Smart Categorization Engine:** Automated logic that parses transaction descriptions against registered keywords to deduce the correct expense category.
- **🗄️ Database Versioning:** Full control over database schema changes using **Flyway**, preventing drift between environments.
- **🐳 Containerized Environment:** Ready-to-deploy setup using Docker and Docker Compose.
- **📊 Robust Reporting:** Optimized queries to retrieve financial summaries and historical data.

## 🛠️ Tech Stack

- **Language:** Java 21 (LTS).
- **Framework:** Spring Boot 3.x (Web, Security, Data JPA).
- **Database:** PostgreSQL.
- **Migrations:** Flyway.
- **Containerization:** Docker & Docker Compose.
- **Build Tool:** Maven.

## 🏗️ Architecture Highlights

This project follows strict backend engineering best practices:

- **Layered Architecture:** Clear separation between Controller, Service, and Repository layers.
- **DTO Pattern:** Usage of Java Records for immutable Data Transfer Objects, ensuring efficient data handling.
- **Exception Handling:** Global exception handling with `@RestControllerAdvice` for consistent API error responses.
