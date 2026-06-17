# CareerCompass

## Overview

CareerCompass is a career guidance and job tracking platform designed to help students and job seekers manage their career journey in a structured manner.

The application allows users to create personalized learning roadmaps, track job applications, monitor progress, discover career resources, and access consultancy information through a centralized platform.

This project was built to strengthen backend development skills using Spring Boot, REST APIs, Spring Security, JWT authentication, JPA/Hibernate, and MySQL.

---

## Features

### User Management

* User registration and login
* Secure password encryption using BCrypt
* JWT-based authentication and authorization

### Roadmap Management

* Create learning roadmaps
* Add and manage tasks
* Track roadmap progress

### Job Application Tracking

* Record job applications
* Track application status
* Maintain application history

### Progress Monitoring

* Store learning progress
* Monitor completed activities
* Generate career progress records

### Resource Management

* Career guidance resources
* Learning materials
* Consultancy recommendations

---

## Technology Stack

### Backend

* Java 21
* Spring Boot
* Spring MVC
* Spring Data JPA
* Spring Security
* Hibernate

### Database

* MySQL

### Build Tool

* Maven

### Authentication

* JWT (JSON Web Token)
* BCrypt Password Hashing

### Development Tools

* STS / Eclipse
* Postman
* Git
* GitHub

---

## Architecture

Client
↓
REST API
↓
Controller Layer
↓
Service Layer
↓
Repository Layer
↓
MySQL Database

The application follows a layered architecture to maintain separation of concerns and improve maintainability.

---

## Database Design

User
├── Roadmap
│ └── Task
│
├── Application
│ └── Consultancy
│
└── ProgressLog

Admin
├── Resource
├── Consultancy
├── GapFriendlyCompany
└── AtsBypassTip

---

## Security Implementation

* Stateless authentication
* JWT token generation and validation
* Custom JWT authentication filter
* Password encryption using BCrypt
* Protected API endpoints
* Role-based access preparation

---

## Key Concepts Implemented

* RESTful API Design
* Spring Security
* JWT Authentication
* Entity Relationships
* JPA/Hibernate ORM
* Validation
* Exception Handling
* DTO Pattern
* Layered Architecture

---

## Learning Outcomes

Through this project I gained hands-on experience with:

* Building production-style REST APIs
* Designing relational database schemas
* Implementing secure authentication systems
* Working with JPA and Hibernate relationships
* Applying layered architecture principles
* Managing project dependencies using Maven
* Version control using Git and GitHub

---

## Future Enhancements

* Email notifications
* Resume analysis module
* AI-powered career recommendations
* Admin dashboard
* Advanced reporting and analytics
* Docker deployment
* Cloud deployment (AWS)

---

## Author

Shivani Kaiche

Java Backend Engineer

