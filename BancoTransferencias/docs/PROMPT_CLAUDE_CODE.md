# Prompt para cerrar la entrega con Claude Code (ejecutar en la carpeta BancoTransferencias)

```
Lee README.md y .cursor/rules/springboot.mdc. El backend (Spring Boot 4.1, Java 21) y el frontend (React + TS) de esta carpeta
ya están escritos pero el backend nunca se compiló. Haz lo siguiente, en orden, y corrige lo que falle sin cambiar las reglas de negocio:

1. Levanta PostgreSQL con `docker compose up -d` (si no hay Docker, usa el perfil h2).
2. En backend/: crea .env desde .env.example, ejecuta `mvn test` y corrige errores de compilación o de pruebas (los 5 tests de TransferServiceTest deben pasar).
3. Arranca el backend (`mvn spring-boot:run`) y prueba con curl: GET /api/cuentas, una transferencia exitosa (1001001001 -> 1001001002, 150000),
   una rechazada por saldo insuficiente (1001001004 -> 1001001001, 9000000), GET /api/transacciones y GET /api/transacciones/{referencia}/qr.
4. En frontend/: `npm install && npm run dev` (puerto 3000) y verifica que la app carga y transfiere.
5. En docs/: `npm i --no-save playwright && npx playwright install chromium && node capturar.mjs` y revisa que las 7 capturas en docs/capturas/ se vean bien.
6. Verifica en PostgreSQL con psql que accounts y transactions guardaron los datos y toma esa evidencia (texto) para el informe.
7. Regenera el informe con `python3 build_informe.py` (variable CHROME si hace falta) y confirma que ya no dice "Captura pendiente".
8. Actualiza la estructura en .cursor/rules/springboot.mdc si agregaste archivos, haz commit y push al repositorio de GitHub.

Al terminar, dime qué corregiste y qué quedó verificado.
```
