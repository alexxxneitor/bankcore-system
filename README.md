# BankCore – Sistema Bancario Distribuido

Proyecto backend desarrollado con arquitectura de microservicios, enfocado en la gestión de clientes y cuentas bancarias.

---

## Arquitectura general

El sistema está compuesto por dos microservicios independientes, cada uno con su propia base de datos y responsabilidades bien definidas. La comunicación entre servicios se realiza a través de HTTP sobre la red interna de Docker.

```
ms-customers (8081) ←── ms-accounts (8082)
       │                        │
  customers_db             accounts_db
       └──────── PostgreSQL ────┘
```

---

## ms-customers — Puerto 8081

Responsable de:

- Registro y gestión de clientes
- Autenticación y generación de JWT
- Gestión del perfil del cliente
- Validación del estado del cliente para otros servicios

### Modelo de base de datos

**Tabla: `customers`**

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| id | UUID | PK | Identificador único del cliente |
| dni | VARCHAR | UNIQUE, NOT NULL | Documento nacional de identidad |
| first_name | VARCHAR | NOT NULL | Nombre del cliente |
| last_name | VARCHAR | NOT NULL | Apellido del cliente |
| email | VARCHAR | UNIQUE, NOT NULL | Correo electrónico |
| password | VARCHAR | NOT NULL | Contraseña encriptada (BCrypt) |
| atm_pin | VARCHAR | NOT NULL | PIN de la tarjeta ATM (BCrypt) |
| phone | VARCHAR | NOT NULL | Número de teléfono |
| address | VARCHAR | NOT NULL | Dirección del cliente |
| role | VARCHAR | NOT NULL | Rol del usuario (CUSTOMER / ADMIN) |
| status | VARCHAR | NOT NULL | Estado del cliente (ACTIVE, BLOCKED, etc.) |
| created_date | TIMESTAMP | NOT NULL | Fecha de creación |
| updated_date | TIMESTAMP | — | Fecha de última actualización |

### Endpoints

| Endpoint | Método | Descripción | Acceso |
|---|---|---|---|
| /api/auth/register | POST | Registro de nuevos clientes | Público |
| /api/auth/login | POST | Login para clientes | Público |
| /api/customers/me | GET | Perfil del cliente autenticado | CUSTOMER, ADMIN |
| /api/customers/{customerId} | GET | Detalles del cliente consultado | ADMIN, SERVICE |
| /api/customers/{customerId}/validate | GET | Estado y existencia del cliente | SERVICE |
| /api/customers/{customerId}/validate-pin | POST | Validación del PIN del cliente | SERVICE |

### Documentación de la API

Con el servicio en ejecución, accede a Swagger UI en:

```
http://localhost:8081/swagger-ui/index.html
```

---

## ms-accounts — Puerto 8082

Responsable de:

- Gestión de cuentas bancarias
- Asociación de cuentas a clientes
- Comunicación con `ms-customers` para validaciones

### Modelo de base de datos

**Tabla: `accounts`**

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| id | UUID | PK, autogenerada | Identificador único de la cuenta |
| accountNumber | String | UNIQUE, max 24 | Número único de la cuenta bancaria |
| customerId | UUID | — | Identificador del cliente propietario |
| accountType | Enum | SAVINGS, CHECKING | Tipo de cuenta bancaria |
| currency | Enum | COP, USD | Moneda de la cuenta |
| balance | BigDecimal | — | Saldo actual de la cuenta |
| alias | String | — | Nombre personalizado de la cuenta |
| status | Enum | ACTIVE, BLOCKED, CLOSED | Estado actual de la cuenta |
| dailyWithdrawalLimit | BigDecimal | — | Límite máximo de retiro diario |
| createdAt | Instant | No actualizable | Fecha y hora de creación |
| updatedAt | Instant | Auto-actualizable | Fecha y hora de última actualización |

Relaciones: `security` → 1:1 con `account_pin_security` · `transactions` → 1:N con `transactions`

**Tabla: `transactions`**

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| id | UUID | PK | Identificador único de la transacción |
| account | UUID | FK → accounts.id | Cuenta asociada |
| type | Enum | DEPOSIT, WITHDRAWAL, TRANSFER | Tipo de transacción |
| amount | BigDecimal | — | Monto de la transacción |
| balanceAfter | BigDecimal | — | Saldo después de la transacción |
| description | String | — | Descripción opcional |
| counterpartyAccountNumber | String | — | Número de cuenta contraparte |
| counterpartyName | String | — | Nombre del contraparte |
| referenceNumber | String | UNIQUE | Referencia generada automáticamente |
| status | Enum | PENDING, COMPLETED, FAILED | Estado de la transacción |
| createdAt | Instant | No actualizable | Fecha y hora de creación |

**Tabla: `transfers`**

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| id | UUID | PK (UUIDv7) | Identificador único de la transferencia |
| sourceAccountId | UUID | FK → accounts.id | Cuenta origen |
| destinationAccountNumber | String | — | Número de cuenta destino |
| amount | BigDecimal | — | Monto transferido |
| createdAt | Instant | No actualizable | Fecha y hora de creación |

**Tabla: `account_pin_security`**

| Campo | Tipo | Descripción |
|---|---|---|
| accountId | UUID | PK, FK → accounts.id |
| failedAttempts | int | Intentos fallidos consecutivos (default 0) |
| temporaryLockUntil | Instant | Bloqueo temporal hasta esta fecha |
| permanentLock | boolean | Bloqueo permanente (default false) |
| lastFailedAttemptAt | Instant | Último intento fallido |

### Endpoints

| Endpoint | Método | Descripción | Acceso |
|---|---|---|---|
| /api/accounts | POST | Registro de cuentas bancarias | CUSTOMER |
| /api/accounts | GET | Cuentas bancarias del cliente | CUSTOMER |
| /api/accounts/{accountId} | GET | Detalles de una cuenta específica | CUSTOMER |
| /api/accounts/{accountId}/deposit | POST | Registra un depósito | CUSTOMER |
| /api/accounts/{accountId}/withdraw | POST | Ejecuta un retiro de fondos | CUSTOMER |
| /api/transfers | POST | Registra una transferencia entre cuentas | CUSTOMER |
| /api/accounts/{accountId}/transactions | GET | Historial de transacciones con filtros | CUSTOMER |

### Documentación de la API

Con el servicio en ejecución, accede a Swagger UI en:

```
http://localhost:8082/swagger-ui/index.html
```

---

## Tecnologías utilizadas

- Java 17
- Spring Boot 3.5
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL 17
- Docker & Docker Compose
- Maven
- Lombok
- MapStruct
- Swagger / OpenAPI

---

## Estructura del repositorio

```
bankcore-system/
├── .github/
│   └── workflows/
│       └── docker-build-push.yml
├── docs/
│   └── Postman/
│       └── Bankcore-Collection.postman_collection.json
├── ms-customers/
│   ├── Dockerfile
│   └── src/
├── ms-accounts/
│   ├── Dockerfile
│   └── src/
├── docker-compose.yml
├── .env.template
└── README.md
```

---

## Ejecutar con imágenes del registry

Esta opción no requiere clonar el repositorio ni compilar el código. Solo necesitas Docker instalado.

### 1. Crear el archivo `.env`

Crea un archivo `.env` en tu directorio de trabajo basándote en la siguiente plantilla:

```env
# Global
SWAGGER_UI_DOCUMENTATION_ENABLED=true
BANKCORESYSTEM_DATASOURCE_USERNAME=username-bankcore-system
BANKCORESYSTEM_DATASOURCE_PASSWORD=password-bankcore-system
BANKCORESYSTEM_SECRET_KEY=YS1zdHJpbmctc2VjcmV0LWF0LGxlYXN0LTI1Ni1iaXRzLWxvbmc=
JPA_DDL_AUTO=update
JPA_SHOW_SQL=false

# ms-accounts
SERVER_PORT_ACCOUNTS=8082
DATASOURCE_DB_NAME_ACCOUNTS=accounts_db
BANKCORESYSTEM_LIMIT_CHECKING=3000.00
BANKCORESYSTEM_LIMIT_SAVINGS=1000.00

# ms-customers
SERVER_PORT_CUSTOMERS=8081
DATASOURCE_DB_NAME_CUSTOMERS=customers_db

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000
CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,PATCH
CORS_MAX_AGE=3600
```

### 2. Crear el `docker-compose.yml`

```yaml
name: bankcore-system

services:
  postgres-datasource:
    image: postgres:17.8
    environment:
      POSTGRES_USER: ${BANKCORESYSTEM_DATASOURCE_USERNAME}
      POSTGRES_PASSWORD: ${BANKCORESYSTEM_DATASOURCE_PASSWORD}
      DATASOURCE_DB_NAME_ACCOUNTS: ${DATASOURCE_DB_NAME_ACCOUNTS}
      DATASOURCE_DB_NAME_CUSTOMERS: ${DATASOURCE_DB_NAME_CUSTOMERS}
    ports:
      - "5432:5432"
    volumes:
      - ./bankcore-system-data:/var/lib/postgresql/data
      - ./init-multiple-db.sh:/docker-entrypoint-initdb.d/init-multiple-db.sh
    restart: always
    networks:
      - bankcore-network

  ms-customers:
    image: sebas679og/ms-customers:latest
    ports:
      - "8081:8081"
    depends_on:
      - postgres-datasource
    environment:
      SERVER_PORT_CUSTOMERS: ${SERVER_PORT_CUSTOMERS}
      SPRING_DATASOURCE_URL_CUSTOMERS: jdbc:postgresql://postgres-datasource:5432/${DATASOURCE_DB_NAME_CUSTOMERS}
      BANKCORESYSTEM_DATASOURCE_USERNAME: ${BANKCORESYSTEM_DATASOURCE_USERNAME}
      BANKCORESYSTEM_DATASOURCE_PASSWORD: ${BANKCORESYSTEM_DATASOURCE_PASSWORD}
      JPA_DDL_AUTO: ${JPA_DDL_AUTO}
      JPA_SHOW_SQL: ${JPA_SHOW_SQL}
      BANKCORESYSTEM_SECRET_KEY: ${BANKCORESYSTEM_SECRET_KEY}
      CORS_ALLOWED_ORIGINS: ${CORS_ALLOWED_ORIGINS}
      CORS_ALLOWED_METHODS: ${CORS_ALLOWED_METHODS}
      CORS_MAX_AGE: ${CORS_MAX_AGE}
      SWAGGER_UI_DOCUMENTATION_ENABLED: ${SWAGGER_UI_DOCUMENTATION_ENABLED}
    restart: on-failure
    networks:
      - bankcore-network

  ms-accounts:
    image: sebas679og/ms-accounts:latest
    ports:
      - "8082:8082"
    depends_on:
      - postgres-datasource
      - ms-customers
    environment:
      SERVER_PORT_ACCOUNTS: ${SERVER_PORT_ACCOUNTS}
      SPRING_DATASOURCE_URL_ACCOUNTS: jdbc:postgresql://postgres-datasource:5432/${DATASOURCE_DB_NAME_ACCOUNTS}
      BANKCORESYSTEM_DATASOURCE_USERNAME: ${BANKCORESYSTEM_DATASOURCE_USERNAME}
      BANKCORESYSTEM_DATASOURCE_PASSWORD: ${BANKCORESYSTEM_DATASOURCE_PASSWORD}
      JPA_DDL_AUTO: ${JPA_DDL_AUTO}
      JPA_SHOW_SQL: ${JPA_SHOW_SQL}
      BANKCORESYSTEM_SECRET_KEY: ${BANKCORESYSTEM_SECRET_KEY}
      CUSTOMER_SERVICE_URL: http://ms-customers:8081
      CORS_ALLOWED_ORIGINS: ${CORS_ALLOWED_ORIGINS}
      CORS_ALLOWED_METHODS: ${CORS_ALLOWED_METHODS}
      CORS_MAX_AGE: ${CORS_MAX_AGE}
      BANKCORESYSTEM_LIMIT_CHECKING: ${BANKCORESYSTEM_LIMIT_CHECKING}
      BANKCORESYSTEM_LIMIT_SAVINGS: ${BANKCORESYSTEM_LIMIT_SAVINGS}
      SWAGGER_UI_DOCUMENTATION_ENABLED: ${SWAGGER_UI_DOCUMENTATION_ENABLED}
    restart: on-failure
    networks:
      - bankcore-network

networks:
  bankcore-network:
    driver: bridge
```

### 3. Levantar el sistema

```bash
docker compose up -d
```

Docker descargará automáticamente las imágenes del registry y levantará los tres contenedores.

### Alternativamente con `docker run`

Si prefieres levantar los servicios manualmente, primero crea la red:

```bash
docker network create bankcore-network
```

Luego levanta PostgreSQL:

```bash
docker run -d \
  --name postgres-datasource \
  --network bankcore-network \
  -e POSTGRES_USER=username-bankcore-system \
  -e POSTGRES_PASSWORD=password-bankcore-system \
  -e DATASOURCE_DB_NAME_ACCOUNTS=accounts_db \
  -e DATASOURCE_DB_NAME_CUSTOMERS=customers_db \
  -p 5432:5432 \
  postgres:17.8
```

Luego ms-customers:

```bash
docker run -d \
  --name ms-customers \
  --network bankcore-network \
  -e SERVER_PORT_CUSTOMERS=8081 \
  -e SPRING_DATASOURCE_URL_CUSTOMERS=jdbc:postgresql://postgres-datasource:5432/customers_db \
  -e BANKCORESYSTEM_DATASOURCE_USERNAME=username-bankcore-system \
  -e BANKCORESYSTEM_DATASOURCE_PASSWORD=password-bankcore-system \
  -e JPA_DDL_AUTO=update \
  -e JPA_SHOW_SQL=false \
  -e BANKCORESYSTEM_SECRET_KEY=YS1zdHJpbmctc2VjcmV0LWF0LGxlYXN0LTI1Ni1iaXRzLWxvbmc= \
  -e CORS_ALLOWED_ORIGINS=http://localhost:3000 \
  -e CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,PATCH \
  -e CORS_MAX_AGE=3600 \
  -e SWAGGER_UI_DOCUMENTATION_ENABLED=true \
  -p 8081:8081 \
  sebas679og/ms-customers:latest
```

Luego ms-accounts:

```bash
docker run -d \
  --name ms-accounts \
  --network bankcore-network \
  -e SERVER_PORT_ACCOUNTS=8082 \
  -e SPRING_DATASOURCE_URL_ACCOUNTS=jdbc:postgresql://postgres-datasource:5432/accounts_db \
  -e BANKCORESYSTEM_DATASOURCE_USERNAME=username-bankcore-system \
  -e BANKCORESYSTEM_DATASOURCE_PASSWORD=password-bankcore-system \
  -e JPA_DDL_AUTO=update \
  -e JPA_SHOW_SQL=false \
  -e BANKCORESYSTEM_SECRET_KEY=YS1zdHJpbmctc2VjcmV0LWF0LGxlYXN0LTI1Ni1iaXRzLWxvbmc= \
  -e CUSTOMER_SERVICE_URL=http://ms-customers:8081 \
  -e CORS_ALLOWED_ORIGINS=http://localhost:3000 \
  -e CORS_ALLOWED_METHODS=GET,POST,PUT,DELETE,PATCH \
  -e CORS_MAX_AGE=3600 \
  -e BANKCORESYSTEM_LIMIT_CHECKING=3000.00 \
  -e BANKCORESYSTEM_LIMIT_SAVINGS=1000.00 \
  -e SWAGGER_UI_DOCUMENTATION_ENABLED=true \
  -p 8082:8082 \
  sebas679og/ms-accounts:latest
```

---

## Puertos expuestos

| Servicio | Puerto |
|---|---|
| ms-customers | 8081 |
| ms-accounts | 8082 |
| postgres-datasource | 5432 |

---

## Pruebas manuales

Se proporciona una colección de Postman con todos los endpoints disponibles, documentación y ejemplos de uso.

[Descargar colección Postman](./docs/Postman/Bankcore-Collection.postman_collection.json)

[![Run in Postman](https://run.pstmn.io/button.svg)](https://web.postman.co/workspace/7881e3dd-6f51-41b2-8795-b96d8e8d79aa/collection/35777093-6228710d-4455-484b-9231-93e45ada5ee9?action=share&source=copy-link&creator=35777093)

---

## Contribución

Consulta [CONTRIBUTING.md](./CONTRIBUTING.md) para conocer el flujo de trabajo, convenciones de ramas, commits y pull requests.