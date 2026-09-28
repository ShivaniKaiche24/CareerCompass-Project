# CareerCompass — AI-Powered Career Guidance REST API

CareerCompass is a **Spring Boot REST API** designed for freshers and candidates with career gaps who need structured and personalised career guidance.

The backend uses **Google Gemini AI** to generate personalised, week-by-week career roadmaps based on a user's profile. It also provides APIs for task management, progress tracking, job application tracking, career resources, and resume optimization.

The project was developed with a focus on **secure REST API design, layered architecture, database relationships, authentication, AI integration, API testing, documentation, debugging, and deployment**.

---

## Why I Built This

After completing CDAC in 2024, I faced a common challenge: deciding which role to target, how to structure my job search, and how to explain a career gap during interviews.

I built CareerCompass to turn these challenges into a structured workflow.

The platform provides career roadmaps, daily tasks, progress tracking, resume optimization, job application tracking, and career resources through a secure REST API.

---

## Key Features

### AI Career Roadmap Generation

* Generates personalised career roadmaps using Google Gemini AI.
* Creates week-by-week career guidance based on the user's profile and target role.
* Stores generated roadmap and task information in the database.

### Career Gap & ATS Optimization

Provides structured tasks and resources focused on:

* Resume improvement
* LinkedIn optimization
* Career-gap presentation
* Direct HR outreach
* Job-search activities

### Resume Optimizer

The API accepts a resume and job description and provides:

* Keyword analysis
* Match analysis
* Match score
* Optimized resume summary

### Task Management

* Retrieve today's tasks
* Mark tasks as completed
* Track roadmap tasks
* Identify overdue tasks
* Track completion progress

### Progress Tracking

* Daily progress updates
* Progress history
* Current streak tracking

### Job Application Tracker

* Create job application records
* Track application status
* View applications
* Track follow-ups

### Career Resources

Provides APIs for:

* Career resources
* Consultancy information
* Gap-friendly company resources

### Authentication & Security

* JWT-based stateless authentication
* Spring Security 6
* BCrypt password hashing
* Protected REST endpoints
* Secure credential configuration using environment variables

---

# Technology Stack

| Category          | Technology                  |
| ----------------- | --------------------------- |
| Language          | Java 17                     |
| Framework         | Spring Boot 3.2.5           |
| Security          | Spring Security 6           |
| Authentication    | JWT / JJWT 0.12.3           |
| Database          | MySQL 8                     |
| ORM               | JPA / Hibernate             |
| AI Integration    | Google Gemini 2.5 Flash API |
| Build Tool        | Maven                       |
| API Documentation | Swagger / OpenAPI           |
| API Testing       | Postman                     |
| Deployment        | Railway                     |
| Version Control   | Git / GitHub                |

---

# Architecture

The application follows a layered Spring Boot architecture.

```text
                    Client
                      |
                      v
             REST Controllers
                      |
                      v
               Service Layer
                      |
                      v
             Repository Layer
                      |
                      v
             MySQL Database


             Security Layer
                    |
                    v
          JWT Authentication Filter
                    |
                    v
             Protected APIs


             AI Integration
                    |
                    v
            Google Gemini API
```

### Controller Layer

Responsible for:

* Receiving HTTP requests
* Validating request data
* Calling appropriate services
* Returning API responses

### Service Layer

Responsible for:

* Business logic
* Transaction management
* Processing application workflows
* Coordinating repositories and external services

### Repository Layer

Responsible for:

* Database operations
* JPA/Hibernate integration
* Entity persistence and retrieval

### Security Layer

Responsible for:

* JWT authentication
* Request authorization
* Password hashing
* Protecting secured endpoints

### AI Integration

Responsible for:

* Sending relevant user information to Google Gemini
* Processing AI-generated career guidance
* Converting AI responses into application data

---

# Authentication Flow

CareerCompass uses JWT-based stateless authentication.

```text
User
 |
 | Register / Login
 v
Authentication API
 |
 v
Credentials Validation
 |
 v
JWT Generated
 |
 v
Client sends JWT with requests
 |
 v
JWT Authentication Filter
 |
 v
Protected REST API
```

Passwords are stored using **BCrypt hashing** rather than plain-text storage.

---

# REST API Endpoints

## Authentication

| Method | Endpoint             | Description                       |
| ------ | -------------------- | --------------------------------- |
| POST   | `/api/auth/register` | Register a new user               |
| POST   | `/api/auth/login`    | Authenticate user and receive JWT |

---

## Career Roadmap

| Method | Endpoint                | Description                   |
| ------ | ----------------------- | ----------------------------- |
| POST   | `/api/roadmap/generate` | Generate an AI career roadmap |
| GET    | `/api/roadmap/active`   | Retrieve the active roadmap   |

---

## Tasks

| Method | Endpoint                   | Description                  |
| ------ | -------------------------- | ---------------------------- |
| GET    | `/api/tasks/today`         | Retrieve today's tasks       |
| PUT    | `/api/tasks/{id}/complete` | Mark a task as completed     |
| GET    | `/api/tasks/roadmap/{id}`  | Retrieve tasks for a roadmap |
| PUT    | `/api/tasks/mark-overdue`  | Mark overdue tasks as missed |

---

## Progress

| Method | Endpoint                | Description                      |
| ------ | ----------------------- | -------------------------------- |
| GET    | `/api/progress/streak`  | Retrieve current progress streak |
| GET    | `/api/progress/history` | Retrieve progress history        |
| POST   | `/api/progress/update`  | Update daily progress            |

---

## Job Applications

| Method | Endpoint                        | Description                 |
| ------ | ------------------------------- | --------------------------- |
| POST   | `/api/applications`             | Create a job application    |
| GET    | `/api/applications`             | Retrieve job applications   |
| PUT    | `/api/applications/{id}/status` | Update application status   |
| GET    | `/api/applications/followups`   | Retrieve today's follow-ups |

---

## Career Resources

| Method | Endpoint             | Description                      |
| ------ | -------------------- | -------------------------------- |
| GET    | `/api/resources`     | Retrieve career resources        |
| GET    | `/api/consultancies` | Retrieve consultancy information |
| GET    | `/api/gap-companies` | Retrieve gap-friendly companies  |

---

## Resume Optimizer

| Method | Endpoint               | Description                              |
| ------ | ---------------------- | ---------------------------------------- |
| POST   | `/api/resume/optimize` | Analyze resume against a job description |

---

# Database Design

The application uses **MySQL 8** with **JPA/Hibernate** for persistence.

The backend contains multiple entities representing:

* Users
* Career roadmaps
* Roadmap tasks
* Progress records
* Job applications
* Resources
* Resume optimization data

Entity relationships are implemented using JPA relationships such as:

```text
User
 |
 +---- Career Roadmap
 |        |
 |        +---- Roadmap Tasks
 |
 +---- Progress Records
 |
 +---- Job Applications
```

DTOs are used for API data transfer to avoid unnecessarily exposing internal entity structures.

---

# AI Integration

CareerCompass integrates the **Google Gemini API** to generate personalised career guidance.

The AI workflow is:

```text
User Profile
     |
     v
Career Goal / Target Role
     |
     v
Backend Service
     |
     v
Google Gemini API
     |
     v
Generated Career Roadmap
     |
     v
Application Processing
     |
     v
Roadmap + Tasks Stored in Database
```

The AI integration replaces hardcoded career-roadmap templates with dynamically generated guidance.

---

# API Testing

The REST API was tested using **Postman**.

Testing covered API workflows including:

* User registration
* User authentication
* JWT-protected requests
* Roadmap generation
* Task management
* Progress tracking
* Job application management
* Resume optimization

Swagger/OpenAPI is also used to document the available API endpoints.

---

# Debugging & Problem Solving

During development, I used systematic debugging to identify and resolve backend issues rather than relying on trial and error.

One significant debugging area involved **Spring Security authentication and authorization**, including:

* Security filter-chain configuration
* JWT authentication
* Understanding `401 Unauthorized` vs `403 Forbidden`
* Correctly configuring protected endpoints

These issues helped strengthen the application's authentication and authorization flow.

---

# Deployment

The backend is deployed using **Railway**.

### Production Base URL

```text
https://careercompass-project-production.up.railway.app
```

### Health Check

```text
https://careercompass-project-production.up.railway.app/actuator/health
```

> Railway free-tier services may sleep after periods of inactivity. The first request after inactivity may therefore take several seconds while the service starts.

---

# Environment Configuration

Sensitive information is not intended to be committed to source control.

The application uses environment/configuration values for information such as:

```text
Database URL
Database username
Database password
JWT secret
Gemini API key
```

For local development, configure these values in your local Spring Boot configuration.

---

# Local Setup

## Prerequisites

Make sure you have:

* Java 17
* Maven
* MySQL 8
* Google Gemini API key
* Git

---

## 1. Clone the Repository

```bash
git clone https://github.com/ShivaniKaiche24/careercompass-backend.git
```

```bash
cd careercompass-backend
```

---

## 2. Create the Database

Open MySQL and run:

```sql
CREATE DATABASE careercompass_db;
```

---

## 3. Configure Application Properties

Copy the example configuration:

```text
application.properties.example
```

to your local:

```text
application.properties
```

Configure your:

* MySQL connection
* JWT secret
* Gemini API key
* Other required environment values

**Do not commit real API keys, passwords, JWT secrets, or other sensitive credentials to GitHub.**

---

## 4. Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

---

# Project Structure

The project follows a standard Spring Boot layered structure.

```text
src/
└── main/
    ├── java/
    │   └── ...
    │       ├── controller/
    │       ├── service/
    │       ├── repository/
    │       ├── entity/
    │       ├── dto/
    │       ├── security/
    │       └── ...
    │
    └── resources/
        └── application.properties
```

---

# Key Engineering Areas

This project provided hands-on experience with:

* Java 17
* Spring Boot 3
* Spring MVC
* Spring Security 6
* JWT authentication
* BCrypt password hashing
* REST API design
* JPA/Hibernate
* MySQL database design
* Entity relationships
* DTO-based API design
* Transaction management
* Google Gemini API integration
* Postman API testing
* Swagger/OpenAPI documentation
* Debugging authentication and authorization issues
* Railway deployment
* Environment-based secret management
* Git/GitHub

---

# What I Personally Built

I completed the backend development lifecycle for CareerCompass, including:

* Requirements analysis
* Backend architecture
* REST API development
* Database/entity design
* Business logic implementation
* JWT authentication
* Spring Security configuration
* Google Gemini API integration
* API testing
* Swagger/OpenAPI documentation
* Debugging and defect resolution
* Production deployment
* Environment and secret configuration

The project was developed from requirements through coding, testing, debugging, and deployment.

---

# Future Improvements

Potential future improvements include:

* Automated unit and integration test coverage
* CI/CD pipeline
* Improved API monitoring
* Additional career-data integrations
* More advanced resume analysis
* Frontend client application
* Improved production observability

---

# Repository

**GitHub Repository**

https://github.com/ShivaniKaiche24/CareerCompass-Project.git

---

# Author

**Shivani Kaiche**

Java / Spring Boot Backend Developer

**LinkedIn:**
https://linkedin.com/in/shivanikaiche

**GitHub:**
https://github.com/ShivaniKaiche24
