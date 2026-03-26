# Ejecución de la aplicación con imagenes alojadas en el Registro Docker

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

### 2. Crear el `init-multiple-db.sh` para la creacion de las bases de datos

```sh
#!/bin/bash
set -e

echo "starting databases: $DATASOURCE_DB_NAME_ACCOUNTS y $DATASOURCE_DB_NAME_CUSTOMERS"

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
  CREATE DATABASE "$DATASOURCE_DB_NAME_ACCOUNTS";
  CREATE DATABASE "$DATASOURCE_DB_NAME_CUSTOMERS";
EOSQL
```

### 3. Crear el `docker-compose.yml`

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

### 4. Levantar el sistema

```bash
docker compose up -d
```

Docker descargará automáticamente las imágenes del registry y levantará los tres contenedores.

Claro, podemos dejarlo más claro, consistente y fácil de leer, resaltando que el script se ejecuta al iniciar el contenedor y que solo se ejecuta la primera vez. Aquí tienes una versión editada:

---

### Alternativa: levantar con `docker run`

Primero, crea la red de Docker para que los servicios puedan comunicarse:

```bash
docker network create bankcore-network
```

Luego levanta PostgreSQL:

Luego, levanta el contenedor de PostgreSQL. Para crear automáticamente las bases de datos `accounts_db` y `customers_db`, se recomienda tener un script de inicialización llamado `init-multiple-db.sh` en el directorio actual de la ejecucion:

```sh
#!/bin/bash
set -e

echo "Creating databases: $DATASOURCE_DB_NAME_ACCOUNTS y $DATASOURCE_DB_NAME_CUSTOMERS"

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "postgres" <<-EOSQL
  CREATE DATABASE "$DATASOURCE_DB_NAME_ACCOUNTS";
  CREATE DATABASE "$DATASOURCE_DB_NAME_CUSTOMERS";
EOSQL

echo "Databases created successfully."
``` 

```bash
docker run -d \
  --name postgres-datasource \
  --network bankcore-network \
  -e POSTGRES_USER=username-bankcore-system \
  -e POSTGRES_PASSWORD=password-bankcore-system \
  -e POSTGRES_DB=postgres \
  -e DATASOURCE_DB_NAME_ACCOUNTS=accounts_db \
  -e DATASOURCE_DB_NAME_CUSTOMERS=customers_db \
  -p 5432:5432 \
  -v $(pwd)/init-multiple-db.sh:/docker-entrypoint-initdb.d/init-multiple-db.sh \
  postgres:17.8
```

> ⚠️ Nota: El script de inicialización solo se ejecuta la **primera vez** que se inicia el contenedor. Si ya existe un volumen con datos, no volverá a crear las bases automáticamente.

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