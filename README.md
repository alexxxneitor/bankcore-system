# BankCore – Sistema Bancario Distribuido

Proyecto backend desarrollado con arquitectura de microservicios, enfocado en la gestión de clientes y cuentas bancarias.

---

## Arquitectura general

El sistema está compuesto por dos microservicios independientes, cada uno con su propia base de datos y responsabilidades bien definidas. La comunicación entre servicios se realiza a través de HTTP sobre la red interna de Docker.

```
ms-customers (8081) ← ms-accounts (8082)
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

- Para ms-customers:

```
http://localhost:8081/swagger-ui/index.html
```

- Para ms-accounts:

```
http://localhost:8082/swagger-ui/index.html
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

Consulta [EXECUTION.md](./EXECUTION.md) para desplegar el sistema con las imagenes alojadas en el registro de Docker.

---

## Contribución

Consulta [CONTRIBUTING.md](./CONTRIBUTING.md) para conocer el flujo de trabajo, convenciones de ramas, commits y pull requests.
