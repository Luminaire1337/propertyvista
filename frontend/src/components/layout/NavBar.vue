<script setup lang="ts">
import PropertyVistaLogo from '../PropertyVistaLogo.vue'
import { siteName } from '@/site'
import useCurrentUser from '@/queries/useCurrentUser'
import UserDropdown from './UserDropdown.vue'
import PrimaryButton from '../PrimaryButton.vue'

const navLinks = [
  { name: 'Nieruchomości', path: '/properties' },
  { name: 'Dodaj ogłoszenie', path: '/new-listing' },
  { name: 'Kup PropertyPoints', path: '/buy-points' },
]

const { data: user, isPending } = useCurrentUser()
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
          <template v-if="isPending">
            <!-- Skeleton loader -->
            <div class="flex items-center space-x-2 px-4 py-2">
              <div class="w-8 h-8 rounded-full bg-gray-300 animate-pulse"></div>
              <div class="h-4 w-24 bg-gray-300 rounded animate-pulse"></div>
            </div>
          </template>
          <template v-else-if="user">
            <UserDropdown />
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
