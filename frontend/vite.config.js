import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (!id.includes('node_modules')) return
          if (id.includes('@element-plus/icons-vue')) return 'vendor-icons'
          if (id.includes('element-plus')) return 'vendor-element-plus'
          if (id.includes('vue') || id.includes('pinia')) return 'vendor-vue'
          return 'vendor'
        }
      }
    },
    chunkSizeWarningLimit: 700
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      '/uploads': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})
