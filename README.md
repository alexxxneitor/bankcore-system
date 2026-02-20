# BankCore – Sistema Bancario Distribuido

Proyecto backend desarrollado con **arquitectura de microservicios**, enfocado en la gestión de clientes y cuentas bancarias, como parte de un proyecto colaborativo por sprints.

> ⚠️ El proyecto se encuentra actualmente en desarrollo (Sprint 1).

---

## 🧩 Arquitectura General

El sistema está compuesto por **dos microservicios independientes**, cada uno con su propia base de datos y responsabilidades bien definidas.  
La comunicación entre servicios se realiza a través de HTTP sobre la red interna de Docker.

### 🔵 ms-customers (Puerto 8081)

Responsable de:

- Registro y gestión de clientes
- Autenticación y generación de JWT
- Gestión del perfil del cliente
- Validación del estado del cliente para otros servicios

🗄 Base de Datos independiente:

- Motor: PostgreSQL
- Base de datos: `customers_db`
- Independiente de otros microservicios
- Sin compartir esquema con otros servicios

---

#### Modelo de base de datos

Tabla: `customers`

| Campo        | Tipo      | Restricciones    | Descripción                                |
|--------------|-----------|------------------|--------------------------------------------|
| id           | UUID      | PK               | Identificador único del cliente            |
| dni          | VARCHAR   | UNIQUE, NOT NULL | Documento nacional de identidad            |
| first_name   | VARCHAR   | NOT NULL         | Nombre del cliente                         |
| last_name    | VARCHAR   | NOT NULL         | Apellido del cliente                       |
| email        | VARCHAR   | UNIQUE, NOT NULL | Correo electrónico                         |
| password     | VARCHAR   | NOT NULL         | Contraseña encriptada (BCrypt)             |
| atm_pin      | VARCHAR   | NOT NULL         | PIN de la tarjeta ATM (BCrypt)             |
| phone        | VARCHAR   | NOT NULL         | Número de teléfono                         |
| address      | VARCHAR   | NOT NULL         | Dirección del cliente                      |
| role         | VARCHAR   | NOT NULL         | Rol del usuario (CUSTOMER / ADMIN)         |
| status       | VARCHAR   | NOT NULL         | Estado del cliente (ACTIVE, BLOCKED, etc.) |
| created_date | TIMESTAMP | NOT NULL         | Fecha de creación                          |
| updated_date | TIMESTAMP |                  | Fecha de última actualización              |

---

#### URLs principales

| Endpoint                |Método| Descripción                |
|-------------------------|------|----------------------------|
| /api/customers/register | POST | Registro de nuevos clientes|

---

#### 📄 Documentación de la API

para acceder a la documentación de la API del microservicio `ms-customers`, una vez que el servicio esté en ejecución, puedes acceder a través de Swagger UI en la siguiente URL:

```url
    http://localhost:8081/swagger-ui/index.html
```

---

### 🟢 ms-accounts (Puerto 8082)

Responsable de:

- Gestión de cuentas bancarias
- Asociación de cuentas a clientes
- Comunicación con `ms-customers` para validaciones

Base de datos independiente:

- PostgreSQL (accounts_db)

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
│
├── ms-customers/        # Microservicio de clientes y autenticación
│   ├── Dockerfile
│   └── src/
│
├── ms-accounts/         # Microservicio de cuentas bancarias
│   ├── Dockerfile
│   └── src/
│
├── docker-compose.yml
├── .env.example
└── README.md
```

## 📋  Requisitos Previos antes del despliegue local

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- Java 17
- Un IDE que soporte Java (IntelliJ IDEA, Eclipse o VS Code)
- Docker Desktop
- Docker Compose (incluido en Docker Desktop)

### ⚙️ Configuración de Variables de Entorno

El proyecto utiliza variables de entorno para la configuración de bases de datos.
Crea un archivo `.env` en la raíz del proyecto basándote en el archivo `.env.template`

> ⚠️ Recordar actualizar el `.env.template` si se agregan nuevas variables.

### 🚀 Despliegue Local con Docker

Desde la raíz del proyecto, ejecuta el siguiente comando:

```bash
docker-compose up --build
```

Este comando:

- Construye las imágenes Docker
- Inicializa los contenedores
- Levanta las bases de datos PostgreSQL
- Arranca ambos microservicios

### 🌐 Puertos Expuestos

| Servicio             | Puerto |
| -------------------- | ------ |
| ms-customers         | 8081   |
| ms-accounts          | 8082   |
| PostgreSQL Customers | 5433   |
| PostgreSQL Accounts  | 5434   |
