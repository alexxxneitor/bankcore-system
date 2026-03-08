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
- Puerto que expone 5432
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

| Endpoint                                  | Método | Descripción                                        | Acceso                                   |
|-------------------------------------------|--------|----------------------------------------------------|------------------------------------------|
| /api/auth/register                        | POST   | Registro de nuevos clientes                        | Publico                                  |
| /api/auth/login                           | POST   | Login para clientes                                | Publico                                  |
| /api/customers/me                         | GET    | Obtener el perfil del cliente autenticado          | Restringido solo roles (CUSTOMER, ADMIN) |
| /api/customers/{customerId}               | GET    | Obtener detalles del cliente consultado            | Restringido solo roles (ADMIN, SERVICE)  |
| /api/customers/{customerId}/validate      | GET    | Obtener estado y existencia del cliente consultado | Restringido solo rol (SERVICE)           |
| /api/customers/{customerId}/validate-pin  | POST   | Obtener validacion del pin del cliente             | Restringido solo rol (SERVICE)           |

---

#### 📄 Documentación de la API

*📄 API Documentation:*

para acceder a la documentación de la API del microservicio `ms-customers`, una vez que el servicio esté en ejecución, puedes acceder a través de Swagger UI en la siguiente URL:

```url
http://localhost:8081/swagger-ui/index.html
```

Swagger UI permite explorar y probar los endpoints directamente desde el navegador.

---

### 🟢 ms-accounts (Puerto 8082)

Responsable de:

- Gestión de cuentas bancarias
- Asociación de cuentas a clientes
- Comunicación con `ms-customers` para validaciones

🗄 Base de Datos independiente:

- Motor: PostgreSQL
- Base de datos: `accounts_db`
- Puerto que expone 5432
- Sin compartir esquema con otros servicios

---

#### Modelo de base de datos

Tabla: `accounts`

### 🗄️ Entidad: Account

| Campo                | Tipo       | Requerido | Restricciones                | Descripción                                               |
|----------------------|------------|-----------|------------------------------|-----------------------------------------------------------|
| id                   | UUID       |  Sí       | Clave primaria, autogenerada | Identificador único de la cuenta                          |
| accountNumber        | String     |  Sí       | Único, longitud máxima 24    | Número único de la cuenta bancaria                        |
| customerId           | UUID       |  Sí       | —                            | Identificador del cliente propietario de la cuenta        |
| accountType          | Enum       |  Sí       | SAVINGS, CHECKING            | Tipo de cuenta bancaria                                   |
| currency             | Enum       |  Sí       | COP, USD                     | Moneda de la cuenta                                       |
| balance              | BigDecimal |  Sí       | —                            | Saldo actual de la cuenta                                 |
| alias                | String     |  Sí       | —                            | Nombre personalizado de la cuenta definido por el usuario |
| status               | Enum       |  Sí       | ACTIVE, BLOCKED, CLOSED      | Estado actual de la cuenta                                |
| dailyWithdrawalLimit | BigDecimal |  Sí       | —                            | Límite máximo de retiro permitido por día                 |
| createdAt            | Instant    |  Sí       | No actualizable              | Fecha y hora en que se creó la cuenta                     |
| updatedAt            | Instant    |  Sí       | Se actualiza automáticamente | Fecha y hora de la última actualización                   |

Los campos `createdAt` y `updatedAt` se gestionan automáticamente mediante el método anotado con `@PrePersist`.

Cuando la entidad se guarda por primera vez en la base de datos:

- `createdAt` se establece con la fecha y hora actual.
- `updatedAt` se establece con la fecha y hora actual.

---

#### URLs principales

| Endpoint                            | Método | Descripción                           | Acceso                             |
|-------------------------------------|--------|---------------------------------------|------------------------------------|
| /api/accounts                       | POST   | Registro de Cuentas Bancarias         | Restringido solo Role CUSTOMER     |
| /api/accounts                       | GET    | Obtener Cuentas Bancarias del cliente | Restringido solo Role CUSTOMER     |

---

#### 📄 Documentación de la API

*📄 API Documentation:*

para acceder a la documentación de la API del microservicio `ms-accounts`, una vez que el servicio esté en ejecución, puedes acceder a través de Swagger UI en la siguiente URL:

```url
http://localhost:8082/swagger-ui/index.html
```

Swagger UI permite explorar y probar los endpoints directamente desde el navegador.

## 🛠️ Tecnologías Utilizadas

- Java 17
- Spring Boot 3.5.11
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Docker & Docker Compose
- Maven
- Lombok
- MapStruct
- Swagger / OpenAPI (en progreso)

---

## 📁 Estructura del Repositorio

```text
bankcore-system/
|
├──docs/                  # Documentacion con Postman
|   └── Postman/
|       └──Bankcore-Collection.postman_collection.json
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

---

## 📋  Requisitos Previos antes del despliegue local

Antes de ejecutar el proyecto, asegúrate de tener instalado:

- Java 17
- Un IDE que soporte Java (IntelliJ IDEA, Eclipse o VS Code)
- Docker Desktop
- Docker Compose (incluido en Docker Desktop)

---

### ⚙️ Configuración de Variables de Entorno

El proyecto utiliza variables de entorno para la configuración de bases de datos.
Crea un archivo `.env` en la raíz del proyecto basándote en el archivo `.env.template`

> ⚠️ Recordar actualizar el `.env.template` si se agregan nuevas variables.

---

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

---

## 🧪 Pruebas Manuales

Para realizar pruebas manuales del servicio, se proporciona una **colección de Postman** que incluye todos los endpoints disponibles junto con su respectiva documentación y ejemplos de uso.

Esta colección permite explorar fácilmente las funcionalidades del servicio, así como validar las diferentes respuestas y escenarios de cada endpoint.

### 📥 Descargar colección

[Descargar Postman Collection](./docs/postman/Bankcore-Collection.postman_collection.json)

### ▶️ Ejecutar en Postman

También puedes importar la colección directamente en tu workspace de Postman utilizando el siguiente botón:

[![Run in Postman](https://run.pstmn.io/button.svg)](https://web.postman.co/workspace/7881e3dd-6f51-41b2-8795-b96d8e8d79aa/collection/35777093-2d47e7ec-516e-47ed-89c7-48d05b9c981b?action=share&source=copy-link&creator=35777093)

---

### 🌐 Puertos Expuestos

| Servicio             | Puerto |
| -------------------- | ------ |
| ms-customers         | 8081   |
| ms-accounts          | 8082   |
| postgres-system-data | 5432   |
