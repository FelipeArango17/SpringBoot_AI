"""Genera Informe_Transferencias_Bancarias.pdf (HTML -> PDF con Chrome headless).
Incluye las capturas de docs/capturas si existen; si no, deja un recuadro "pendiente".
Uso: python3 build_informe.py   (variable CHROME para indicar el ejecutable de Chrome)"""
import os, pathlib, subprocess, html

here = pathlib.Path(__file__).parent.resolve()
chrome = os.environ.get("CHROME") or next((p for p in [
    "/usr/bin/google-chrome", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"] if pathlib.Path(p).exists()), "google-chrome")

def fig(name, caption):
    f = here / "capturas" / name
    body = f'<img src="capturas/{name}">' if f.exists() else '<div class="pending">Captura pendiente: ejecutar docs/capturar.mjs</div>'
    return f'<figure>{body}<figcaption>{html.escape(caption)}</figcaption></figure>'

evid_file = here / "evidencia_postgres.txt"
evid = (f'<p>Consulta real con <code>psql</code> tras las transferencias de prueba (una exitosa y una rechazada):</p><pre>{html.escape(evid_file.read_text(encoding="utf-8"))}</pre>'
        if evid_file.exists() else '<div class="pending">Evidencia pendiente: guardar la salida de psql en docs/evidencia_postgres.txt</div>')

doc = f"""<!doctype html><html lang="es"><meta charset="utf-8"><style>
@page {{ size: Letter; margin: 22mm 18mm; }}
body {{ font: 11pt/1.5 system-ui, 'Helvetica Neue', Arial, sans-serif; color:#1f2937; }}
h1 {{ color:#0f4c81; font-size:24pt; margin:0 0 4px; }} h2 {{ color:#0f4c81; border-bottom:2px solid #d9e0ea; padding-bottom:4px; margin-top:26px; }}
h3 {{ color:#0a3558; }} .sub {{ color:#6b7280; margin-bottom:18px; }}
table {{ border-collapse:collapse; width:100%; font-size:10pt; margin:8px 0; }} th,td {{ border:1px solid #d9e0ea; padding:6px 8px; text-align:left; vertical-align:top; }} th {{ background:#f3f6fa; }}
code,pre {{ font-family: ui-monospace, Menlo, monospace; font-size:9pt; }} pre {{ background:#f6f9fc; border:1px solid #d9e0ea; border-radius:6px; padding:8px; white-space:pre-wrap; }}
figure {{ margin:14px 0; text-align:center; break-inside:avoid; }} figure img {{ max-width:100%; border:1px solid #d9e0ea; border-radius:6px; }}
figcaption {{ font-size:9.5pt; color:#6b7280; margin-top:4px; }} .pending {{ border:2px dashed #b91c1c; color:#b91c1c; padding:40px 10px; border-radius:6px; }}
</style>
<h1>Informe — Transferencias entre cuentas bancarias</h1>
<div class="sub">Laboratorio Spring Boot con IA · Aplicaciones FullStack · Tecnológico de Antioquia · 2026<br>Estudiante: Felipe Arango · Ejercicio propuesto (entrega 30 de octubre de 2026)</div>

<h2>1. Objetivo</h2>
<p>Construir una aplicación completa para trasladar dinero entre cuentas de un banco que muestre el <b>histórico de transacciones</b> y, al
generar el resultado de cada transacción, un <b>código QR</b> con los datos básicos de la misma codificados. Se siguieron los pasos del laboratorio
(reglas → configuración → entidades → repositorio → servicio → controlador → CORS → frontend → documentación).</p>

<h2>2. Stack tecnológico</h2>
<table><tr><th>Capa</th><th>Tecnología</th></tr>
<tr><td>Backend</td><td>Java 21 (compatible con 25), Spring Boot 4.1, Spring Web MVC, Spring Data JPA/Hibernate, Bean Validation</td></tr>
<tr><td>Base de datos</td><td>PostgreSQL 16 (H2 en memoria para pruebas y perfil <code>h2</code>)</td></tr>
<tr><td>QR</td><td>ZXing 3.5.4 (PNG de 300×300, UTF-8)</td></tr>
<tr><td>API docs</td><td>springdoc-openapi (Swagger UI)</td></tr>
<tr><td>Frontend</td><td>React 19, TypeScript, Vite, Axios</td></tr>
<tr><td>Herramientas</td><td>Maven, Git/GitHub, Claude Code (reglas en <code>.cursor/rules/springboot.mdc</code>)</td></tr></table>

<h2>3. Arquitectura</h2>
<pre>React (:3000) ──Axios──▶ Controller ──▶ Service ──▶ Repository ──▶ PostgreSQL (accounts, transactions)
                          @Valid/HTTP     reglas + QR    Spring Data JPA</pre>
<p>Paquete <code>co.edu.tdea.banktransfer</code>: <code>controller</code>, <code>service</code>, <code>repository</code>, <code>entity</code>,
<code>dto</code> (records), <code>exception</code> (jerarquía sealed + <code>GlobalExceptionHandler</code>) y <code>config</code> (CORS y datos de ejemplo).</p>
<h3>Modelo de datos</h3>
<table><tr><th>Tabla</th><th>Columnas principales</th></tr>
<tr><td>accounts</td><td>id, account_number (único), owner_name, balance DECIMAL(19,2), created_at, version (@Version)</td></tr>
<tr><td>transactions</td><td>id, reference (UUID único), source_account, target_account, amount DECIMAL(19,2), description, status (COMPLETED | REJECTED), failure_reason, created_at</td></tr></table>

<h2>4. Reglas de negocio y decisiones de diseño</h2>
<ul><li>Las cuentas deben existir (404), ser distintas y el monto mayor que cero (400), y el origen debe tener saldo suficiente (422).</li>
<li>Dinero con <code>BigDecimal</code>; débito, crédito y registro en <b>una sola transacción</b> de base de datos.</li>
<li>Bloqueo pesimista de las cuentas siempre en el mismo orden (evita <i>deadlocks</i> en transferencias cruzadas).</li>
<li>Los intentos rechazados hacen rollback y se guardan aparte (<code>REQUIRES_NEW</code>) como <code>REJECTED</code> con su motivo, así quedan en el histórico.</li>
<li>Errores como <code>ProblemDetail</code> (RFC 9457); mensajes en español.</li></ul>

<h2>5. Código QR</h2>
<p><code>QrCodeService</code> arma un texto con referencia, cuenta origen, cuenta destino, monto en COP, fecha y estado, y lo codifica con ZXing
(corrección de errores M). La API lo devuelve como <i>data URI</i> en la respuesta de <code>POST /api/transferencias</code> y como PNG en
<code>GET /api/transacciones/{{referencia}}/qr</code>. El frontend lo muestra en un modal descargable y desde el histórico (“Ver QR”).</p>

<h2>6. API REST</h2>
<table><tr><th>Método</th><th>Ruta</th><th>Descripción</th><th>Códigos</th></tr>
<tr><td>GET</td><td>/api/cuentas</td><td>Lista cuentas</td><td>200</td></tr>
<tr><td>GET</td><td>/api/cuentas/{{numero}}</td><td>Consulta una cuenta</td><td>200, 404</td></tr>
<tr><td>POST</td><td>/api/cuentas</td><td>Abre una cuenta</td><td>201, 400, 409</td></tr>
<tr><td>POST</td><td>/api/transferencias</td><td>Transfiere y devuelve QR</td><td>201, 400, 404, 422</td></tr>
<tr><td>GET</td><td>/api/transacciones?cuenta=</td><td>Histórico</td><td>200</td></tr>
<tr><td>GET</td><td>/api/transacciones/{{referencia}}/qr</td><td>PNG del QR</td><td>200, 400</td></tr></table>

<h2>7. Evidencias (capturas)</h2>
{fig("01-inicio.png","Pantalla inicial: cuentas con saldo, formulario de transferencia e histórico.")}
{fig("02-transferencia-exitosa.png","Transferencia exitosa con su código QR y los datos codificados.")}
{fig("03-transferencia-rechazada.png","Transferencia rechazada por saldo insuficiente (toast con el detalle del backend).")}
{fig("04-historico.png","Histórico con transacciones completadas y rechazadas.")}
{fig("05-qr-historico.png","QR de un movimiento abierto desde el histórico.")}
{fig("06-swagger.png","Swagger UI con los endpoints documentados.")}
{fig("07-vista-movil.png","Vista adaptada a pantalla móvil.")}

<h2>8. Verificación</h2>
<table><tr><th>Prueba</th><th>Cómo se verifica</th></tr>
<tr><td>Pruebas de integración (5): éxito, saldo insuficiente, misma cuenta, cuenta inexistente, filtro del histórico</td><td><code>cd backend &amp;&amp; mvn test</code></td></tr>
<tr><td>Frontend compila sin errores de tipos</td><td><code>cd frontend &amp;&amp; npm run build</code></td></tr>
<tr><td>Datos guardados en PostgreSQL</td><td><code>SELECT * FROM accounts; SELECT * FROM transactions;</code></td></tr></table>
{evid}

<h2>9. Prompts utilizados</h2>
<p>La secuencia completa de prompts está en <code>docs/PROMPTS.md</code>.</p>

<h2>10. Conclusiones</h2>
<p>El flujo del laboratorio (reglas → capas → frontend) se aplicó a un dominio con mayor criticidad: al manejar dinero fue necesario cuidar la
precisión (<code>BigDecimal</code>), la atomicidad y la concurrencia, además de conservar los intentos fallidos en el histórico.
La IA aceleró la generación de las capas, mientras que las reglas de negocio y las decisiones de diseño se revisaron y justificaron.</p>
</html>"""
(here / "informe.html").write_text(doc, encoding="utf-8")
out = here / "Informe_Transferencias_Bancarias.pdf"
subprocess.run([chrome, "--headless=new", "--no-sandbox", "--disable-gpu", "--no-pdf-header-footer",
                f"--print-to-pdf={out}", (here / "informe.html").as_uri()], check=True, capture_output=True)
(here / "informe.html").unlink()
print("Generado", out)
