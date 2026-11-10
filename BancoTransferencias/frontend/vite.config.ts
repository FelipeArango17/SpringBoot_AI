import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// El filtro CORS del backend solo permite http://localhost:3000, por eso el servidor de desarrollo usa ese puerto.
export default defineConfig({
  plugins: [react()],
  server: { port: 3000, strictPort: true },
})
