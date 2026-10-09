# SastaHai — Full-Stack E-Commerce Platform

[![Java](https://img.shields.io/badge/Java-21-E55A24?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-Atlas-47A248?style=flat-square&logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![Razorpay](https://img.shields.io/badge/Razorpay-Payment_Gateway-0284C7?style=flat-square&logo=razorpay&logoColor=white)](https://razorpay.com/)
[![React](https://img.shields.io/badge/React-19+-0284C7?style=flat-square&logo=react&logoColor=white)](https://react.dev/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-4.x-38B2AC?style=flat-square&logo=tailwindcss&logoColor=white)](https://tailwindcss.com/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=flat-square&logo=docker&logoColor=white)](https://www.docker.com/)

A modern, high-performance full-stack e-commerce marketplace built with **Java 21 Spring Boot**, **MongoDB Atlas**, and a responsive **React (Tailwind CSS)** frontend. The platform features secure Google OAuth2 and email-based authentication, real-time OTP verification, Razorpay payment processing with cryptographic signature verification, dynamic inventory management, and automated email notifications.

---

## Architecture Overview

```
                      +-----------------------------------+
                      |      React SPA Frontend (Vite)    |
                      |  Tailwind CSS + Context API State |
                      +-----------------+-----------------+
                                        |  REST APIs / JSON
                                        v
+---------------------------------------+---------------------------------------+
|                       Spring Boot 3 Backend Server                            |
|                                                                               |
|  [Security / OAuth2 Filter]   [REST Controllers]    [Global Exception Handler]|
|                                       |                                       |
|  [Business Services]                  v                                       |
|   ├── UserService          ├── OrderService         ├── OtpService            |
|   ├── ProductService       ├── PaymentService       └── EmailService          |
|                                                                               |
|  [Spring Data Mongo Repositories]                                             |
+-------------------+-------------------+-------------------+-------------------+
                    |                   |                   |
                    v                   v                   v
           [MongoDB Atlas]         [Razorpay API]      [Gmail SMTP Server]
            Database Cluster       Payment Gateway       Email Dispatcher
```

---

## Core Features

### 1. Authentication & Security
- **Dual Authentication Modes:** Native email/password authentication using BCrypt encryption alongside one-click Google OAuth2.0 login.
- **Pre-Registration Email Verification:** Real-time email verification check to alert existing users before sending verification codes.
- **Persistent OTP Service:** 6-digit OTP verification backed by a 10-minute expiry window, thread-safe memory caching, and MongoDB persistence across server restarts.
- **Role-Based Access Control (RBAC):** Dedicated administrative routes (`/add-product`) guarded by user role validation.

### 2. Product Catalog & Inventory
- **Real-Time Category Filtering & Search:** Instant search across product titles and category segmentation (Electronics, Clothing, Grocery).
- **Atomic Stock Management:** Real-time inventory tracking ensuring products cannot be purchased or added beyond available quantity.
- **Admin Inventory Dashboard:** Dedicated admin portal to create, update, and manage catalog items with image URL integration.

### 3. Checkout & Payment Gateway
- **Razorpay Integration:** Seamless checkout using Razorpay Checkout SDK.
- **Cryptographic Signature Verification:** Backend HMAC-SHA256 signature verification validating transaction legitimacy before order confirmation.
- **Order Idempotency:** Prevention of duplicate order submissions for already processed transactions.

### 4. User Profile & Account Management
- **Profile Customization:** Modify personal contact numbers, delivery addresses, and birthdates.
- **Avatar Upload Support:** Local image upload with automatic Base64 conversion and cloud URL support.
- **Secure Password Modification:** Current password verification required prior to setting a new encrypted password.
- **Account Deletion:** User-controlled account removal with permanent database record cleanup and automatic session teardown.

### 5. Automated Communications
- **Transaction Receipts:** Immediate HTML/Plain-text order confirmation dispatched to buyers.
- **Admin Alerts:** Instant automated notification to administrators for every placed order with customer contact details.

---

## Tech Stack

| Layer | Technologies |
| :--- | :--- |
| **Backend Framework** | Java 21, Spring Boot 3.x, Spring Security 6, Spring Data MongoDB |
| **Frontend Framework** | React 18, Vite, Tailwind CSS, React Router DOM (HashRouter) |
| **State Management** | React Context API (`AuthContext`, `CartContext`, `ToastContext`, `SearchContext`) |
| **Database** | MongoDB Atlas / Local MongoDB Cluster |
| **Payment Gateway** | Razorpay REST API & SDK |
| **Mailing Service** | Spring Boot Starter Mail (JavaMailSender via Gmail SMTP) |
| **Testing Suite** | JUnit 5, Mockito, Spring MockMvc |
| **Containerization** | Docker (Multi-stage build), Nginx Alpine |

---

## API Reference

### Authentication & Users (`/users`)
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/users/check-email?email={email}` | Verify if email already exists | Public |
| `POST` | `/users/register` | Register a verified user account | Public |
| `POST` | `/users/login` | Authenticate with email and password | Public |
| `GET` | `/users/by-email?email={email}` | Retrieve profile for OAuth flow | Public |
| `PUT` | `/users/update/{id}` | Update profile, photo, or password | Authenticated |
| `DELETE` | `/users/delete/{id}` | Permanently delete account | Authenticated |

### One-Time Password (`/api/otp`)
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/otp/send` | Generate and dispatch 6-digit OTP code | Public |
| `POST` | `/api/otp/verify` | Validate submitted OTP code | Public |

### Products (`/products`)
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/products/all` | Fetch complete catalog items | Public |
| `GET` | `/products/{id}` | Fetch individual product details | Public |
| `POST` | `/products/add` | Insert new inventory item | Admin Only |
| `DELETE` | `/products/{id}` | Delete product item | Admin Only |

### Payments & Orders (`/api/payment` & `/orders`)
| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/payment/create-order` | Initialize order with Razorpay | Public |
| `POST` | `/orders/place/{userId}` | Validate signature and save order | Authenticated |
| `GET` | `/orders/user/{userId}` | Retrieve purchase history for user | Authenticated |
| `GET` | `/orders/all-orders` | Fetch all platform orders | Admin Only |

---

## Environment Variables

### Backend Configuration (`src/main/resources/application.properties`)
```properties
# MongoDB Connection
spring.data.mongodb.uri=${MONGO_LOCAL_TEST}

# Gmail SMTP Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USER_TEST}
spring.mail.password=${EMAIL_APP_PASS_TEST}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Google OAuth2 Credentials
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID_TEST}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET_TEST}
spring.security.oauth2.client.registration.google.redirect-uri=${REDIRECT_URL_TEST}

# Razorpay API Credentials
razorpay.key.id=${RAZORPAY_ID_TEST}
razorpay.key.secret=${RAZORPAY_SECRET_TEST}

# Client Integration
api.external-service.url=${FRONTEND_URL_TEST}
```

### Frontend Configuration (`.env`)
```properties
VITE_RAZORPAY_KEY_ID=your_razorpay_key_id
```

---

## Local Development Setup

### Prerequisites
- JDK 21 installed
- Node.js 18+ and npm installed
- MongoDB running locally or a MongoDB Atlas URI

### 1. Backend Setup
```bash
# Navigate to the backend directory
cd backend

# Run the Spring Boot application
mvn spring-boot:run
```
Backend will start on `http://localhost:8080`.

### 2. Frontend Setup
```bash
# Navigate to the frontend directory
cd frontend

# Install dependencies
npm install

# Start Vite development server
npm run dev
```
Frontend will be accessible at `http://localhost:5173`.

---

## Running Automated Tests

Run the complete JUnit 5 and Mockito test suite:
```bash
cd backend
mvn test
```
Test results verify controller routing, user authentication, OTP lifecycle, and order idempotency without external network requirements.

---

## Docker Deployment

### Multi-Stage Build & Push to Docker Hub

```bash
# Authenticate to Docker Hub
docker login

# Build & Push Backend Container
cd backend
docker build -t <your-dockerhub-username>/sastahai-backend:v1.0 .
docker push <your-dockerhub-username>/sastahai-backend:v1.0

# Build & Push Frontend Container
cd ../frontend
docker build -t <your-dockerhub-username>/sastahai-frontend:v1.0 .
docker push <your-dockerhub-username>/sastahai-frontend:v1.0
```

---

## 👨‍💻 Author
**Rajnish Chauhan**

Java Backend & AI Integration Engineer

🌐 [Portfolio: rajnishsystems.in](https://rajnishsystems.in)
