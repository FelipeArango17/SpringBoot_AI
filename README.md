# SpringBoot_AI — Sistema de Reservas

Laboratorio **Spring Boot con IA** · Aplicaciones FullStack · Tecnológico de Antioquia 2026.

Aplicación full stack para gestionar reservas de servicios: se pueden **listar**, **crear** y **cancelar**
reservas, sin permitir dos reservas activas en la misma fecha y hora.

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Backend | Java 21, Spring Boot 4.1, Spring Web MVC, Spring Data JPA (Hibernate) |
| Base de datos | PostgreSQL 17 |
| Documentación API | SpringDoc OpenAPI (Swagger UI) |
| Frontend | React 19, TypeScript, Vite, Axios |
| Herramientas | Maven, DevTools, IntelliJ IDEA (backend), Cursor (frontend), Git/GitHub |

> El documento del curso sugiere Java 25; el proyecto usa Java 21 (el JDK instalado). Cambiar a 25 solo requiere
> modificar `java.version` en `backend/pom.xml`.

## Arquitectura

```
┌────────────────────┐   HTTP/JSON    ┌───────────────────────────────┐   JPA    ┌────────────┐
│ React (:3000)      │ ─────────────▶ │ Spring Boot (:8080)           │ ───────▶ │ PostgreSQL │
│ Table · Form · Toast│  Axios + CORS │ Controller → Service → Repo   │          │ :5432      │
└────────────────────┘                └───────────────────────────────┘          └────────────┘
```

**Backend (arquitectura en capas)** — paquete `co.edu.tdea.reservation`:

| Paquete | Responsabilidad |
|---|---|
| `controller` | Endpoints REST y códigos HTTP |
| `service` | Reglas de negocio (horario libre, cancelación) |
| `repository` | Acceso a datos con Spring Data JPA |
| `entity` | Entidad `Reservation` y enum `ReservationStatus` |
| `dto` | `record`s de entrada/salida (`ReservationRequest`, `ReservationResponse`, `ErrorResponse`) |
| `exception` | Excepciones de negocio, de datos inválidos y `GlobalExceptionHandler` |
| `config` | Filtro CORS para `http://localhost:3000` |

**Frontend** — `frontend/src`: `components/` (tabla, formulario, toast), `services/ReservationService.ts`
(único punto de acceso a la API) y `types/`.

### Reglas de negocio
- Una reserva requiere `customerName`, `date`, `time` y `service`.
- No se puede crear una reserva si ya existe una **activa** en la misma fecha y hora.
- Cancelar es un *soft delete*: la fila se conserva con estado `CANCELLED` y el horario queda libre.
- Cancelar una reserva ya cancelada devuelve 409; si no existe, 404.
- Los mensajes de error de la API están en español.

## API REST

Base: `http://localhost:8080/reservas` · Swagger UI: http://localhost:8080/swagger-ui/index.html

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| GET | `/reservas` | Lista todas las reservas | 200 |
| POST | `/reservas` | Crea una reserva | 201 · 400 (datos faltantes) · 409 (horario ocupado) |
| DELETE | `/reservas/{id}` | Cancela una reserva | 204 · 404 · 409 (ya cancelada) |

Ejemplo de cuerpo para `POST`:
```json
{ "customerName": "Ana", "date": "2026-11-05", "time": "14:30", "service": "Masaje" }
```
Los errores devuelven `{ "status": 409, "message": "..." }`.

## Puesta en marcha

### Requisitos
JDK 21+, Node.js 24+, PostgreSQL 17 en el puerto 5432.

### 1. Base de datos
Crear la base `reservationtdea` (usuario `postgres`):
```bash
psql -U postgres -c "CREATE DATABASE reservationtdea;"
```
Hibernate crea/actualiza las tablas automáticamente (`ddl-auto=update`).

### 2. Backend
```bash
cd backend
cp .env.example .env      # y ajustar DB_PASSWORD
./mvnw spring-boot:run    # o ejecutar ReservationApplication desde IntelliJ
```
El archivo `.env` se lee con `spring.config.import` y **no se sube a git**. El directorio de trabajo debe ser `backend/`.

### 3. Frontend
```bash
cd frontend
cp .env.example .env      # VITE_API_URL=http://localhost:8080
npm install
npm run dev               # http://localhost:3000
```
El puerto 3000 es obligatorio: es el único origen permitido por el filtro CORS.

## Estructura del repositorio
```
SpringBoot_AI/
├── .cursor/rules/springboot.mdc   # reglas de Cursor (siempre activas)
├── backend/                       # API Spring Boot
├── frontend/                      # SPA React
└── README.md
```

## Uso de IA
El proyecto se construyó siguiendo el documento del laboratorio, con prompts por capa (propiedades, entidad,
repositorio, servicio, controlador, CORS, servicio React, tabla y formulario). Las reglas del asistente están en
`.cursor/rules/springboot.mdc`.
