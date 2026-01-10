import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue(), vueDevTools(), tailwindcss()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  envDir: '../',
  build: {
    rollupOptions: {
      output: {
        assetFileNames: 'assets/[hash][extname]',
        chunkFileNames: 'assets/[hash].js',
        entryFileNames: 'assets/[hash].js',
        manualChunks: {
          'vue-vendor': ['vue', 'vue-router'],
          'vue-query': ['@tanstack/vue-query'],
          'ui-libs': ['@headlessui/vue', 'vue-sonner'],
          icons: ['lucide-vue-next'],
        },
      },
    },
  },
})
