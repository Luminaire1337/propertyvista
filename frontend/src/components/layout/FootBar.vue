<script setup lang="ts">
import { computed } from 'vue'
import PropertyVistaLogo from '../PropertyVistaLogo.vue'
import { siteName, siteDescription } from '@/site'

const footerLinks = [
  { name: 'Polityka Prywatności', path: '/privacy-policy' },
  { name: 'Regulamin', path: '/terms-of-service' },
]

const currentYear = computed(() => new Date().getFullYear())
</script>

<template>
  <!-- Footer bar with proper HTML semantics + RODO/Rules page links -->
  <footer class="bg-gray-900 text-gray-300 z-10 mt-auto">
    <!-- Footer content -->
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <!-- Site name and description -->
      <div class="text-center mb-6">
        <div class="flex items-center justify-center mb-4">
          <PropertyVistaLogo :size="24" />
          <span class="text-lg font-bold text-white ml-2">{{ siteName }}</span>
        </div>
        <p>
          {{ siteDescription }}
        </p>
      </div>

      <!-- Aforementioned links in a line divided by pipes -->
      <div class="flex justify-center space-x-4 text-sm text-gray-400">
        <template v-for="(link, index) in footerLinks" :key="link.name">
          <RouterLink :to="link.path" class="hover:underline">{{ link.name }}</RouterLink>
          <span v-if="index < footerLinks.length - 1">|</span>
        </template>
        <span>|</span>
        <span>© {{ siteName }} {{ currentYear }}</span>
      </div>
    </div>
  </footer>
</template>
