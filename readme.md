# 🚚 DeliGuy – Microservices-Based Food Delivery Platform

DeliGuy is a **microservices-based food delivery system** designed to simulate a real-world delivery application (similar to GrabFood / Foodpanda). The project focuses on **clean architecture, scalability, and asynchronous communication** using modern backend technologies.

This repository is primarily built for **learning, experimentation, and portfolio demonstration**, with real-world design patterns applied.

---

## 📌 Project Goals

* Design a **scalable microservices architecture**
* Practice **JWT-based authentication & authorization**
* Use **event-driven communication** between services
* Implement **clean separation of responsibilities**
* Gain hands-on experience with **Kafka, Spring Boot, and API Gateway**

---

## 🧩 System Architecture

```
Client (Web / Mobile)
        ↓
API Gateway
        ↓
-------------------------------------------------
| Auth Service | Order Service | Restaurant Service |
-------------------------------------------------
                ↓
              Kafka
                ↓
          Other Services (Future)
```

### Architecture Style

* **Microservices**
* **Event-driven (Kafka)**
* **Stateless services**
* **Centralized authentication**

---

## 🔐 Authentication Flow

* Users authenticate via **Auth Service**
* JWT is generated and returned to the client
* Client sends JWT in `Authorization: Bearer <token>`
* API Gateway validates JWT before routing requests

---

## 📦 Services Overview

### 1️⃣ API Gateway

**Responsibilities**:

* Entry point for all client requests
* JWT validation
* Request routing to internal services

**Tech**:

* Spring Cloud Gateway

---

### 2️⃣ Auth Service

**Responsibilities**:

* User registration & login
* JWT creation and validation
* Refresh token handling

**Tech**:

* Spring Boot
* Spring Security
* JWT

---

### 3️⃣ Order Service

**Responsibilities**:

* Create and manage orders
* Persist order data
* Publish `OrderCreatedEvent` to Kafka

**Key Endpoint**:

```http
POST /orders
Authorization: Bearer <JWT>
Content-Type: application/json
```

**Event Published**:

* `order-created`

---

### 4️⃣ Restaurant Service (In Progress)

**Responsibilities**:

* Consume order events
* Validate restaurant availability
* Accept or reject orders

---

## 📨 Event-Driven Communication (Kafka)

Kafka is used for **asynchronous communication** between services.

### Why Kafka?

* Loose coupling between services
* Better scalability
* Fault tolerance
* Event replay capability

### Example Flow

```
Order Service → Kafka → Restaurant Service
```

---

## 🛠️ Tech Stack

### Backend

* Java 17+
* Spring Boot
* Spring Security
* Spring Cloud Gateway
* Spring Kafka

### Messaging

* Apache Kafka

### Database

* MySQL / H2 (per service)

### DevOps

* Docker
* Docker Compose
* Maven

---

## 🧪 Testing Strategy

### Manual Testing

* Postman / curl for API testing
* Kafka CLI for event verification

### Planned

* Integration tests
* Embedded Kafka tests
* Contract testing

---

## ▶️ Running the Project Locally

### Prerequisites

* Java 17+
* Maven
* Docker & Docker Compose

### Steps

1. Start Kafka

```bash
docker compose up -d
```

2. Start services

```bash
mvn spring-boot:run
```

3. Test APIs using Postman

---

## 📂 Project Structure (Example)

```
DeliGuy/
├── gateway-service/
├── auth-service/
├── order-service/
├── restaurant-service/
├── docker-compose.yml
└── README.md
```

---

## 🔮 Future Enhancements

* Payment Service
* Delivery / Rider Service
* Saga Pattern (Order lifecycle)
* Circuit Breaker (Resilience4j)
* Centralized logging (ELK)
* Distributed tracing (Zipkin)

---

## 📚 Learning Outcomes

* Microservices communication patterns
* JWT security in distributed systems
* Kafka-based event architecture
* Real-world backend system design

---

## 👤 Author

**YOON MOH MOH AUNG**
Backend Developer | Java | Spring Boot | Microservices

---

## ⭐ Notes

This project is **not production-ready** and is intended for **educational and portfolio purposes**.

Contributions, feedback, and suggestions are
