# ClaimTrack

A small insurance claims portal built to learn full stack development
with React and Spring Boot — including a deliberate legacy-code
refactoring exercise with characterization tests.

## Stack
- Frontend: React 18, TypeScript, Vite
- Backend: Java 21, Spring Boot 3, Spring Data JPA, H2
- Testing: JUnit 5, MockMvc (integration), Mockito (unit)

## Features
- Submit claims with server-side validation (Bean Validation, DTOs)
- Status workflow with enforced transitions
  (SUBMITTED → IN_REVIEW → APPROVED/REJECTED)
- Full test pyramid: unit tests on business logic,
  integration tests on the HTTP layer

## The refactoring exercise
The status-update feature was first written deliberately as a
monolithic controller method, then pinned with characterization
tests (including a real 500-instead-of-400 bug), refactored into
a service layer with transition rules on the enum — tests green
throughout — and only then was the bug fixed, as a separate,
test-verified change. See the commit history.

## Run it
Backend:  cd backend && ./mvnw spring-boot:run   → http://localhost:8080
Frontend: cd frontend && npm install && npm run dev → http://localhost:5173