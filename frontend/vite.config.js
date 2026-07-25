import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  base: './',
  build: {
    outDir: '../docs',
    rolldownOptions:{
      output:{
        chunkFileNames: (info) => {
         //以_开头的js，在github里获取不到，这里统一添加js
          if (info.name.indexOf('_') === 0) {
            return 'assets/js[name]-[hash].js'
          } else {
            return 'assets/[name]-[hash].js'
          }
        }
      }
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
