// Toma las capturas del informe contra la app corriendo (backend :8080 con datos de ejemplo + frontend :3000).
// Uso:  cd docs && npm i --no-save playwright && npx playwright install chromium && node capturar.mjs
import { chromium } from 'playwright'
import { fileURLToPath } from 'node:url'

const out = fileURLToPath(new URL('./capturas/', import.meta.url))
const app = process.env.APP_URL ?? 'http://localhost:3000'
const api = process.env.API_URL ?? 'http://localhost:8080'

const browser = await chromium.launch()
const page = await browser.newPage({ viewport: { width: 1280, height: 900 } })
const shot = (name, opts = {}) => page.screenshot({ path: `${out}${name}.png`, ...opts })

await page.goto(app)
await page.getByText('Laura Gómez').first().waitFor()
await shot('01-inicio', { fullPage: true })

// Transferencia exitosa
await page.selectOption('#source', '1001001001')
await page.selectOption('#target', '1001001002')
await page.fill('#amount', '150000')
await page.fill('#description', 'Pago arriendo')
await page.getByRole('button', { name: 'Transferir' }).click()
await page.getByRole('dialog').waitFor()
await page.waitForTimeout(500)
await shot('02-transferencia-exitosa')
await page.getByRole('dialog').locator('button.btn', { hasText: 'Cerrar' }).click()

// Transferencia rechazada (saldo insuficiente)
await page.selectOption('#source', '1001001004')
await page.selectOption('#target', '1001001001')
await page.fill('#amount', '9000000')
await page.getByRole('button', { name: 'Transferir' }).click()
await page.getByRole('alert').waitFor()
await shot('03-transferencia-rechazada')

// Histórico y QR de un movimiento
await page.getByRole('alert').waitFor({ state: 'hidden', timeout: 10000 }) // el toast se cierra solo a los 5 s
const history = page.locator('section.card', { has: page.getByRole('heading', { name: 'Histórico de transacciones' }) })
await history.scrollIntoViewIfNeeded()
await history.screenshot({ path: `${out}04-historico.png` })
await page.getByRole('button', { name: 'Ver QR' }).first().click()
await page.getByRole('dialog').waitFor()
await page.waitForTimeout(500)
await shot('05-qr-historico')
await page.getByRole('dialog').locator('button.btn', { hasText: 'Cerrar' }).click()

// Swagger
await page.goto(`${api}/swagger-ui.html`)
await page.locator('.opblock').first().waitFor()
await page.waitForTimeout(1000)
await shot('06-swagger', { fullPage: true })

// Vista móvil
await page.setViewportSize({ width: 390, height: 844 })
await page.goto(app)
await page.getByText('Laura Gómez').first().waitFor()
await shot('07-vista-movil', { fullPage: true })

await browser.close()
console.log('Capturas guardadas en', out)
