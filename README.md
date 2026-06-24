# Subscription Project (Full Stack Java / Angular)

A full-stack web application for managing an insurance subscription workflow. Users can create clients, generate quotes, and convert approved quotes into contracts — all governed by an automated validation engine.

---

## 🚀 Business Rules

| Rule | Condition | Result |
|------|-----------|--------|
| Auto-approval | Amount **≤ €10,000** | Status → `APPROVED` |
| Manager review | Amount **> €10,000** | Status → `PENDING_MANAGER` |
| Contract creation | Quote must be `APPROVED` | Otherwise throws an error |

---

## 🛠 Tech Stack

### Backend — Java Spring Boot

| Layer | Technology |
|-------|------------|
| Language | Java 17+ |
| Framework | Spring Boot (Web, Data JPA) |
| Database | H2 / PostgreSQL (via Hibernate) |
| Validation | Jakarta Bean Validation (Hibernate Validator) |
| Architecture | Controller → Service → Repository → Entity/DTO |
| Tools | Maven, Lombok, MapStruct |

### Frontend — Angular

| Layer | Technology |
|-------|------------|
| Language | TypeScript |
| Framework | Angular 16/17+ |
| Async | RxJS for reactive API streams |
| Forms | Angular Reactive Forms with custom validators |
| Styling | Bootstrap / Angular Material |
| HTTP | HttpClient for RESTful communication |

---

## 📡 API Endpoints

All endpoints are prefixed with `/api`.

### Clients

| Method | Endpoint | Description | Key Logic |
|--------|----------|-------------|-----------|
| `POST` | `/api/clients` | Create a new client | Validates unique email and required fields |
| `GET` | `/api/clients` | List all clients | Returns all registered clients |

### Quotes

| Method | Endpoint | Description | Key Logic |
|--------|----------|-------------|-----------|
| `POST` | `/api/quotes` | Create a quote | Amount > €10k → `PENDING_MANAGER` |
| `GET` | `/api/quotes` | List all quotes | Filterable by status (optional) |
| `PATCH` | `/api/quotes/{id}/approve` | Approve a quote | `PENDING_MANAGER` → `APPROVED` |

### Contracts

| Method | Endpoint | Description | Key Logic |
|--------|----------|-------------|-----------|
| `POST` | `/api/contracts` | Create a contract | Fails if quote is not `APPROVED` |
| `GET` | `/api/contracts` | List all contracts | Shows active insurance contracts |

---

## 🖥 Getting Started

### Prerequisites

- JDK 17 or higher
- Node.js & NPM
- Angular CLI — `npm install -g @angular/cli`
- Maven

### Backend Setup

```bash
cd backend
mvn spring-boot:run
```

Server starts at `http://localhost:8080`

### Frontend Setup

```bash
cd frontend
npm install
ng serve
```

Open your browser at `http://localhost:4200`

---

## 📐 Data Model

### Client
| Field | Notes |
|-------|-------|
| `id` | Primary key |
| `name` | |
| `email` | Unique |
| `phone` | |

### Product
| Field | Notes |
|-------|-------|
| `id` | Primary key |
| `code` | |
| `label` | |
| `type` | |

### Quote
| Field | Notes |
|-------|-------|
| `id` | Primary key |
| `clientId` | FK → Client |
| `productId` | FK → Product |
| `amount` | Determines approval flow |
| `status` | `DRAFT` · `PENDING_MANAGER` · `APPROVED` · `REJECTED` |
| `createdAt` | |

### Contract
| Field | Notes |
|-------|-------|
| `id` | Primary key |
| `quoteId` | FK → Quote (must be `APPROVED`) |
| `contractNumber` | |
| `effectiveDate` | |
| `status` | |