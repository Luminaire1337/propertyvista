<script setup lang="ts">
import PropertyVistaLogo from '../PropertyVistaLogo.vue'
import { siteName } from '@/site'
import { useAuthStore } from '@/stores/auth'
import UserDropdown from './UserDropdown.vue'
import PrimaryButton from '../PrimaryButton.vue'

const navLinks = [
  { name: 'Nieruchomości', path: '/properties' },
  { name: 'Dodaj ogłoszenie', path: '/new-listing' },
  { name: 'Kup PropertyPoints', path: '/buy-points' },
]

const authStore = useAuthStore()
</script>

<template>
  <!-- Navigaion bar with site logo and links to main subpages + User profile dropdown or Login button -->
  <header class="bg-gray-100 text-gray-800 shadow-md z-10 blur-backdrop-filter backdrop-blur-sm">
    <!-- Navigation bar content -->
    <div class="mx-auto px-4 sm:px-6 lg:px-8">
      <!-- Site name and logo -->
      <div class="flex items-center justify-between h-16">
        <div class="flex items-center space-x-6">
          <RouterLink
            to="/"
            class="flex items-center space-x-2 hover:opacity-80 transition-opacity"
          >
            <PropertyVistaLogo :size="32" />
            <span class="text-xl font-bold">{{ siteName }}</span>
          </RouterLink>

          <!-- Navigation links -->
          <nav class="flex items-center space-x-6">
            <RouterLink
              v-for="link in navLinks"
              :key="link.path"
              :to="link.path"
              class="text-gray-700 hover:text-gray-900 transition-colors"
            >
              {{ link.name }}
            </RouterLink>
          </nav>
        </div>

        <!-- User profile dropdown (component from HeadlessUI) or Login button -->
        <div>
          <template v-if="authStore.isAuthenticated">
            <UserDropdown :auth-store="authStore" />
          </template>
          <template v-else>
            <RouterLink to="/login">
              <PrimaryButton>Zaloguj się</PrimaryButton>
            </RouterLink>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>
