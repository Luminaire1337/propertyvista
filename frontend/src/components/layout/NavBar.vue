<script setup lang="ts">
import { ref } from 'vue'
import { Menu, X } from 'lucide-vue-next'
import PropertyVistaLogo from '../PropertyVistaLogo.vue'
import { siteName } from '@/site'
import useCurrentUser from '@/queries/useCurrentUser'
import UserDropdown from './UserDropdown.vue'
import PrimaryButton from '../PrimaryButton.vue'
import { useLogoutMutation } from '@/mutations/auth'
import AvatarImage from '../AvatarImage.vue'

const navLinks = [
  { name: 'Nieruchomości', path: '/properties' },
  { name: 'Dodaj ogłoszenie', path: '/new-listing' },
  { name: 'Kup PropertyPoints', path: '/buy-points' },
]

const { data: user, isPending } = useCurrentUser()
const isMobileMenuOpen = ref(false)
const logoutMutation = useLogoutMutation()

const handleLogout = () => {
  logoutMutation.mutate()
  isMobileMenuOpen.value = false
}
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

          <!-- Desktop Navigation links -->
          <nav class="hidden md:flex items-center space-x-6">
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
        <div class="hidden md:block">
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

        <!-- Mobile menu button -->
        <div class="md:hidden flex items-center">
          <button
            @click="isMobileMenuOpen = !isMobileMenuOpen"
            class="text-gray-700 hover:text-gray-900 focus:outline-none"
          >
            <Menu v-if="!isMobileMenuOpen" class="w-6 h-6" />
            <X v-else class="w-6 h-6" />
          </button>
        </div>
      </div>
    </div>

    <!-- Mobile menu -->
    <div v-if="isMobileMenuOpen" class="md:hidden bg-white border-t border-gray-200">
      <div class="px-2 pt-2 pb-3 space-y-1 sm:px-3">
        <RouterLink
          v-for="link in navLinks"
          :key="link.path"
          :to="link.path"
          class="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50"
          @click="isMobileMenuOpen = false"
        >
          {{ link.name }}
        </RouterLink>
      </div>
      <div class="pt-4 pb-4 border-t border-gray-200">
        <div class="px-4 flex items-center">
          <template v-if="isPending">
            <div class="flex items-center space-x-2">
              <div class="w-8 h-8 rounded-full bg-gray-300 animate-pulse"></div>
              <div class="h-4 w-24 bg-gray-300 rounded animate-pulse"></div>
            </div>
          </template>
          <template v-else-if="user">
            <div class="w-full">
              <div class="flex items-center mb-3">
                <div class="shrink-0">
                  <AvatarImage
                    :src="user.avatarImagePath ?? undefined"
                    alt="Awatar użytkownika"
                    :size="40"
                  />
                </div>
                <div class="ml-3">
                  <div class="text-base font-medium text-gray-800">
                    {{ user.firstName }} {{ user.lastName }}
                  </div>
                  <div class="text-sm font-medium text-gray-500">{{ user.email }}</div>
                </div>
              </div>
              <div class="space-y-1">
                <RouterLink
                  to="/my-listings"
                  class="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50"
                  @click="isMobileMenuOpen = false"
                >
                  Moje ogłoszenia
                </RouterLink>
                <RouterLink
                  to="/settings"
                  class="block px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50"
                  @click="isMobileMenuOpen = false"
                >
                  Ustawienia konta
                </RouterLink>
                <button
                  @click="handleLogout"
                  class="block w-full text-left px-3 py-2 rounded-md text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50"
                >
                  Wyloguj się
                </button>
              </div>
            </div>
          </template>
          <template v-else>
            <RouterLink to="/login" @click="isMobileMenuOpen = false" class="block w-full">
              <PrimaryButton class="w-full justify-center">Zaloguj się</PrimaryButton>
            </RouterLink>
          </template>
        </div>
      </div>
    </div>
  </header>
</template>
