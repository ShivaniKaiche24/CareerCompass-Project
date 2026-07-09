# CareerCompass — AI-Powered Career Guidance API

A Spring Boot REST API built for freshers and gap candidates who need
structured career guidance. Instead of generic advice, CareerCompass
generates personalised week-by-week roadmaps using Google Gemini AI,
tracks daily progress, and helps gap candidates bypass ATS screening.

## Why I Built This

After completing CDAC in 2024, I had no clear direction — which role
to target, which consultancies were genuine, how to explain my career
gap in interviews. I built CareerCompass to solve exactly those problems.

## Features

- **AI Roadmap Generation** — Google Gemini generates personalised
  career roadmaps for any IT role based on user profile
- **ATS Bypass Injection** — Gap candidates get resume formatting,
  LinkedIn optimisation, and direct HR outreach tasks as Week 1-2
- **Resume Optimizer** — Paste a job description, get keyword analysis,
  match score, and an optimised summary (my own feature idea)
- **Task Management** — Daily tasks, completion tracking, streak counter
- **Job Application Tracker** — Log applications, track status pipeline,
  follow-up reminders
- **Resource Directory** — Verified resources, consultancy fraud checker,
  gap-friendly company finder
- **JWT Authentication** — BCrypt password hashing, stateless token auth

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Security | Spring Security + JWT (JJWT 0.12.3) |
| Database | MySQL 8 + JPA/Hibernate |
| AI | Google Gemini 2.5 Flash API |
| Build | Maven |
| API Docs | Swagger UI |

## API Endpoints

### Authentication
- `POST /api/auth/register` — Register new user
- `POST /api/auth/login` — Login, receive JWT token

### Roadmap
- `POST /api/roadmap/generate` — Generate AI roadmap
- `GET /api/roadmap/active` — Get active roadmap

### Tasks
- `GET /api/tasks/today` — Today's tasks
- `PUT /api/tasks/{id}/complete` — Mark task complete
- `GET /api/tasks/roadmap/{id}` — All roadmap tasks
- `PUT /api/tasks/mark-overdue` — Mark overdue as MISSED

### Progress
- `GET /api/progress/streak` — Current streak
- `GET /api/progress/history` — Progress history
- `POST /api/progress/update` — Update daily log

### Applications
- `POST /api/applications` — Log job application
- `GET /api/applications` — View all applications
- `PUT /api/applications/{id}/status` — Update status
- `GET /api/applications/followups` — Today's follow-ups

### Resources
- `GET /api/resources` — Resource directory
- `GET /api/consultancies` — Consultancy fraud checker
- `GET /api/gap-companies` — Gap-friendly companies

### Resume Optimizer (AI Feature)
- `POST /api/resume/optimize` — Analyze resume vs job description

## Setup Instructions

1. Clone the repository
```bash
   git clone https://github.com/YOUR_USERNAME/careercompass-backend.git
```

2. Create MySQL database
```sql
   CREATE DATABASE careercompass_db;
```

3. Copy `application.properties.example` to `application.properties`
   and fill in your values

4. Get a free Gemini API key from `aistudio.google.com`

5. Run the application
```bash
   mvn spring-boot:run
```

6. API is available at `http://localhost:8000`
   Swagger docs at `http://localhost:8000/swagger-ui.html`

## Architecture
Controller Layer  →  receives HTTP requests
Service Layer     →  business logic
Repository Layer  →  database via JPA
Security Layer    →  JWT filter on every request
AI Layer          →  Gemini API integration

## 🚀 Live Deployment

**Base URL:** `https://careercompass-project.up.railway.app`

- Swagger UI: `https://careercompass-project.up.railway.app/swagger-ui/index.html`
- Health check: `https://careercompass-project.up.railway.app/actuator/health`

> Note: Free-tier Railway apps sleep after inactivity — first request may take 5–10s to wake up.



## Author

**Shivani Kaiche** 
Java Backend Developer

LinkedIn: linkedin.com/in/shivanikaiche
GitHub: github.com/ShivaniKaiche24
