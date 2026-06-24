#Subscription Project (Full Stack Java/Angular)

A full-stack web application designed to manage an insurance subscription workflow. The system allows users to create clients, generate insurance quotes, and convert approved quotes into official contracts based on specific business rules.

#🚀 Business Logic Overview
The core of this project is the automated validation engine:

Automatic Approval: Quotes with an amount 
≤
≤ 10,000 € are automatically set to APPROVED.
Managerial Workflow: Quotes with an amount 
>
> 10,000 € are set to PENDING_MANAGER status.
Contract Creation: A contract can only be generated if the associated quote has reached the APPROVED status.
#🛠 Tech Stack
Backend (Java Spring Boot)
Language: Java 17+
Framework: Spring Boot (Web, Data JPA)
Database: H2 / PostgreSQL (via Hibernate)
Validation: Jakarta Bean Validation (Hibernate Validator)
Architecture: Layered Architecture (Controller 
→
→ Service 
→
→ Repository 
→
→ Entity/DTO)
Tools: Maven, Lombok (to reduce boilerplate), MapStruct (for DTO mapping).
Frontend (Angular)
Language: TypeScript
Framework: Angular 16/17+
Reactive Programming: RxJS for handling asynchronous API streams.
Forms: Angular Reactive Forms with custom validators.
Styling: Bootstrap / Angular Material (choose the one you used).
HTTP: HttpClient for RESTful communication with the backend.
📡 API Documentation (Backend Endpoints)
All endpoints are prefixed with /api.

Clients
Method    Endpoint    Description    Key Logic
POST    /api/clients    Create a new client    Validates unique email and mandatory fields.
GET    /api/clients    List all clients    Returns a list of all registered clients.
Quotes (Devis)
Method    Endpoint    Description    Key Logic
POST    /api/quotes    Create a quote    If amount 
>
10
k
>10k, status = PENDING_MANAGER.
GET    /api/quotes    List all quotes    Can be filtered by status (optional).
PATCH    /api/quotes/{id}/approve    Approve a quote    Transitions status from PENDING_MANAGER 
→
→ APPROVED.
Contracts (Contrats)
Method    Endpoint    Description    Key Logic
POST    /api/contracts    Create a contract    Throws error if the quote is not APPROVED.
GET    /api/contracts    List all contracts    Shows details of active insurance contracts.
#🖥️ Getting Started
Prerequisites
JDK 17 or higher
Node.js & NPM
Angular CLI (npm install -g @angular/cli)
Maven
Backend Setup
Navigate to the backend folder: cd backend (or your specific folder name)
Run the application:
mvn spring-boot:run
The server will start on http://localhost:8080
Frontend Setup
Navigate to the frontend folder: cd frontend
Install dependencies:
npm install
Start the development server:
ng serve
Open your browser at http://localhost:4200
#📐 Data Model (Simplified)
Client: id, name, email (unique), phone.
Product: id, code, label, type.
Quote: id, clientId, productId, amount, status (DRAFT, PENDING_MANAGER, APPROVED, REJECTED), createdAt.
Contract: id, quoteId, contractNumber, effectiveDate, status.

