# Prompts utilizados

Prompts empleados con la IA (Claude Code) para construir la aplicación, siguiendo la secuencia del laboratorio
(reglas → configuración → entidades → repositorios → servicios → controladores → CORS → frontend → documentación).

## 0. Tarea inicial

```
En el documento "Spring Boot AI" solo falta el ejercicio propuesto: genera una aplicación completa para hacer el
traslado de dinero entre cuentas en un banco. Debe mostrar el histórico de transacciones y, cuando genere el
resultado de la transacción, un código QR con los datos básicos de la transacción codificados.
Revisa lo que ya está construido (sistema de reservas en SpringBoot_AI) y reutiliza sus convenciones.
Trabaja en la carpeta BancoTransferencias dentro de SpringBoot_AI. Entrega código, informe con capturas y prompts,
con la misma estructura de entrega del repo BancoTransferencias-TdeA.
```

## 1. Proyecto base (Spring Initializr)

Maven · Java · Spring Boot 4.1.x · Group `co.edu.tdea` · Artifact `banktransfer` · Jar · Java 21.
Dependencias: Spring Web, Spring Data JPA, PostgreSQL Driver, Validation, SpringDoc OpenAPI, ZXing, H2 (pruebas/perfil `h2`).

## 2. Reglas de Cursor

```
Crea un archivo de reglas para cursor (mdc) llamado 'springboot' que siempre se aplique, con las buenas practicas
de Java 25 en el desarrollo de un servicio en spring boot para que las aplique en el desarrollo del proyecto.
Ten en cuenta que los nombres de clases, métodos y atributos deben ser en ingles.
Cada método público debe tener su documentación Javadoc actualizada.
Para los DTO usamos java records. El dinero se maneja siempre con BigDecimal.
Agrega la estructura actual del proyecto dentro de las reglas.
```

## 3. application.properties

```
Crea la configuración para la conexión a la base de datos postgresql de nombre 'banktdea' con el usuario 'postgres'
y contraseña 'root' (valores desde .env con esos valores por defecto). Habilita la actualización del esquema
automático en hibernate, el dialecto JPA e imprime las sentencias sql de JPA en consola.
Agrega la propiedad app.cors.allowed-origin y un perfil 'h2' para correr sin instalar PostgreSQL.
```

## 4. Entidades

```
Crea dos entidades JPA para un sistema bancario.

Account (tabla accounts): id Long autogenerado, accountNumber String único (6 a 20 dígitos), ownerName,
balance BigDecimal (precision 19, scale 2), createdAt asignado en @PrePersist, version con @Version,
y métodos debit(amount) y credit(amount).

Transaction (tabla transactions, funciona como histórico): id, reference String UUID único generado en @PrePersist,
sourceAccount, targetAccount, amount BigDecimal, description opcional, status (enum TransactionStatus con
COMPLETED y REJECTED), failureReason y createdAt.

Generar getters y constructores. Usa las anotaciones JPA correctas.
```

## 5. Repositorios

```
Crea los repositorios Spring Data JPA para Account y Transaction.
AccountRepository: buscar por número, buscar por número con bloqueo PESSIMISTIC_WRITE, verificar si existe un número
y listar ordenado por número.
TransactionRepository: listar todo de la más reciente a la más antigua, listar donde una cuenta es origen o destino,
y buscar por referencia.
```

## 6. Excepciones y manejo de errores

```
Crea una jerarquía sealed de excepciones de negocio que extienda RuntimeException y que cada una defina su HttpStatus:
AccountNotFoundException (404), DuplicateAccountException (409), InsufficientFundsException (422),
InvalidTransferException (400). Los mensajes deben estar en español.
Crea un @RestControllerAdvice que devuelva ProblemDetail (RFC 9457) para estas excepciones y para los errores de
validación de @Valid, incluyendo un timestamp.
```

## 7. DTOs

```
Crea los DTOs como java records con validaciones Jakarta y mensajes en español: AccountRequest, AccountResponse
(método estático from), TransferRequest (amount >= 0.01, description opcional de máximo 120 caracteres),
TransactionResponse (from) y TransferResponse(transaction, qrCode como data URI PNG, qrContent).
```

## 8. Servicio de QR

```
Crea un servicio QrCodeService con ZXing que construya un texto con los datos básicos de la transacción (referencia,
cuenta origen, cuenta destino, monto en pesos colombianos, fecha y estado), genere el PNG del QR de 300x300 con
codificación UTF-8 y lo devuelva como bytes y como data URI en base64. Agrega las dependencias al pom.xml.
```

## 9. Capa Service

```
Crea AccountService (listar, consultar por número, crear sin repetir número) y TransferService:
- Transferir solo si las dos cuentas existen, son distintas, el monto es mayor que cero y hay saldo suficiente.
- Débito, crédito y registro en una sola transacción de BD, bloqueando las cuentas siempre en el mismo orden.
- Si una regla falla: rollback, guardar el intento como REJECTED con su motivo en una transacción aparte
  (REQUIRES_NEW) y relanzar la excepción.
- Devolver la transacción con su código QR; consultar el histórico con filtro opcional por cuenta; buscar por referencia.
```

## 10. Capa Controller

```
Crea los controladores REST: AccountController (/api/cuentas: GET, GET /{numero}, POST) y TransferController
(/api: POST /transferencias, GET /transacciones?cuenta=, GET /transacciones/{referencia}/qr con image/png).
Devuelve códigos HTTP adecuados y documenta con anotaciones de OpenAPI.
```

## 11. Datos de ejemplo

```
Crea un CommandLineRunner que, si la tabla de cuentas está vacía, inserte cuatro cuentas de ejemplo con saldos en
pesos colombianos. Que se pueda desactivar con app.seed-data.
```

## 12. Pruebas

```
Crea pruebas de integración con @SpringBootTest y H2 que verifiquen: transferencia exitosa (mueve el dinero y
devuelve QR); saldo insuficiente (se rechaza, no cambia el saldo y queda REJECTED en el histórico); misma cuenta;
cuenta inexistente; y filtro del histórico por cuenta.
```

## 13. Filtro CORS

```
Crea una clase Configuration para crear un filtro de CORS que se aplicará a todas las rutas (/**). Debe aceptar el
origen de app.cors.allowed-origin (http://localhost:3000) y permitir todos los métodos y cabeceras necesarios.
```

## 14. Servicio del frontend

```
Usando @.cursor/rules/springboot.mdc como regla, crea un proyecto React + TypeScript con Vite en el puerto 3000 y un
servicio BankService con AXIOS que tenga métodos para: obtener cuentas, crear cuenta, transferir, obtener el
histórico (filtro opcional por cuenta) y obtener la URL del QR de una transacción. Usa la variable VITE_API_URL
y extrae el mensaje de error del ProblemDetail (campo detail).
```

## 15. Vistas del frontend

```
Crea la interfaz del banco en React (cada componente con su .tsx y su .css): panel de cuentas con saldo y alta de
cuenta; formulario de transferencia con selects de origen/destino, monto y descripción (validaciones en el cliente);
modal con el QR de la transferencia (descargable) y su texto; tabla del histórico con estado, motivo del rechazo,
filtro por cuenta y botón "Ver QR"; y un toast para los errores del backend.
```

## 16. Documentación

```
Genera un README.md completo con el stack, la arquitectura, la API, cómo ejecutarlo y las decisiones de diseño.
Genera el informe del ejercicio con capturas y este archivo con los prompts utilizados.
```
