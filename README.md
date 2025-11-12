Secure Notes Application

A secure note-taking application focused on robust authentication and authorization using modern security standards. This project demonstrates JWT, OAuth2, and role-based access control (RBAC) while being fully Dockerized for easy deployment and local development.

Features

User Authentication

Sign up and log in using email/password

Login via OAuth2 (Google, GitHub, etc.)

Two-Factor Authentication (2FA) support

JWT-based authentication for secure token handling

Authorization

Role-based access control (Admin, User)

Admins can manage users and notes

Users can only access their own notes

Secure Notes

CRUD operations for notes

Notes stored securely in PostgreSQL

MongoDB optionally used for flexible storage if needed

Dockerized

Entire application runs in Docker

Includes PostgreSQL, pgAdmin, and your Spring Boot app

Environment variables for credentials and secrets

Technologies Used

Backend: Java, Spring Boot, Spring Security

Database: PostgreSQL, MongoDB

Authentication: JWT, OAuth2, 2FA

Containerization: Docker, Docker Compose

Additional Tools: pgAdmin, Maven, Git

Getting Started
Prerequisites

Docker & Docker Compose installed

Java 17+ (if building locally)

Maven (if building locally)
