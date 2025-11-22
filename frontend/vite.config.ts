import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
	plugins: [react()],
	resolve: {
		alias: {
			'@src': new URL('./src', import.meta.url).pathname,
			'@components': new URL('./src/components', import.meta.url).pathname,
			'@styles': new URL('./src/styles', import.meta.url).pathname,
		},
	},
})
