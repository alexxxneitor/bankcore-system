# BankCore – Sistema Bancario Distribuido

Proyecto backend desarrollado con **arquitectura de microservicios**, enfocado en la gestión de clientes y cuentas bancarias, como parte de un proyecto colaborativo por sprints.

> ⚠️ El proyecto se encuentra actualmente en desarrollo (Sprint 1).

---

## 🧩 Arquitectura General

El sistema está compuesto por **dos microservicios independientes**, cada uno con su propia base de datos y responsabilidades bien definidas:

### 🔵 ms-customers (Puerto 8081)

Responsable de:

- Registro de clientes
- Autenticación y generación de JWT
- Gestión del perfil del cliente
- Validación del estado del cliente para otros servicios

### 🟢 ms-accounts (Puerto 8082)

Responsable de:

- Gestión de cuentas bancarias
- Asociación de cuentas a clientes
- Comunicación con `ms-customers` para validaciones

---

## 🛠️ Tecnologías Utilizadas

- Java 17
- Spring Boot 3.5.10
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- OpenFeign (comunicación entre microservicios)
- Docker & Docker Compose
- Maven
- Lombok
- Swagger / OpenAPI (en progreso)

---

## 📁 Estructura del Repositorio

```text
bankcore-system/
├── ms-customers/     # Microservicio de clientes y autenticación
├── ms-accounts/      # Microservicio de cuentas bancarias
├── docker-compose.yml
└── README.md
