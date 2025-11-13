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


                  Dockerized

                                    Entire application runs in Docker
                                    
                                    Includes PostgreSQL, pgAdmin, and Spring Boot app
                                    
                                    Environment variables for credentials and secrets

                  Technologies Used

                                    Backend: Java, Spring Boot, Spring Security
                                    
                                    Database: PostgreSQL
                                    
                                    Authentication: JWT, OAuth2, 2FA
                                    
                                    Containerization: Docker, Docker Compose
                                    
                                    Additional Tools: pgAdmin, Maven, Git

                Getting Started
                Prerequisites

                                  Docker & Docker Compose installed
                                  
                                  Java 17+ (if building locally)
                                  
                                  Maven (if building locally)

                                  git clone https://github.com/Sylvester-Onyebuchi/Secure-note-app.git
                                  cd Secure-note-app

                                  Create a .env file based on .env.example with your secrets:
                                  SPRING_DATASOURCE_URL=
                                  SPRING_DATASOURCE_USERNAME=
                                  SPRING_DATASOURCE_PASSWORD=
                                  GOOGLE_CLIENT_ID=
                                  GOOGLE_CLIENT_SECRET=
                                  GITHUB_CLIENT_ID=
                                  GITHUB_CLIENT_SECRET=
                                  JWT_SECRET=

                                  Build and run the application with Docker Compose:

                                  docker-compose up --build

                                  Access the app:

                                Spring Boot API: http://localhost:8080
                                
                                pgAdmin: http://localhost:9000

                                Docker Details

Spring Boot App: Runs on port 8080

PostgreSQL: Port 5432 inside container (mapped to 5437 locally if needed)

pgAdmin: Port 80 inside container (mapped to 9000 locally)

All credentials and secrets are read from the .env file for security.


Security Highlights

JWT tokens are stored securely and used for authentication

OAuth2 integration for external login providers

Role-based authorization ensures proper access control

Fully containerized for reproducibility and secure deployment


