<script setup lang="ts">
import { RouterLink } from 'vue-router'
import PrimaryButton from '../PrimaryButton.vue'
import AvatarImage from '../AvatarImage.vue'
import useCurrentUser from '@/queries/useCurrentUser'
import { useLogoutMutation } from '@/mutations/auth'

defineProps<{
  navLinks: { name: string; path: string }[]
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const { data: user, isPending } = useCurrentUser()
const logoutMutation = useLogoutMutation()

const handleLogout = () => {
  logoutMutation.mutate()
  emit('close')
}
</script>

<template>
  <div class="md:hidden bg-white border-t border-gray-200">
    <div class="pt-2 pb-3 space-y-1">
      <RouterLink
        v-for="link in navLinks"
        :key="link.path"
        :to="link.path"
        class="block px-4 py-2 text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 transition-colors"
        @click="emit('close')"
      >
        {{ link.name }}
      </RouterLink>
    </div>
    <div class="pt-4 pb-4 border-t border-gray-200">
      <div>
        <template v-if="isPending">
          <div class="flex items-center space-x-2 px-4">
            <div class="w-8 h-8 rounded-full bg-gray-300 animate-pulse"></div>
            <div class="h-4 w-24 bg-gray-300 rounded animate-pulse"></div>
          </div>
        </template>
        <template v-else-if="user">
          <div class="w-full">
            <div class="flex items-center mb-3 px-4">
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
                class="block px-4 py-2 text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 transition-colors"
                @click="emit('close')"
              >
                Moje ogłoszenia
              </RouterLink>
              <RouterLink
                to="/settings"
                class="block px-4 py-2 text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 transition-colors"
                @click="emit('close')"
              >
                Ustawienia konta
              </RouterLink>
              <button
                @click="handleLogout"
                class="block w-full text-left px-4 py-2 text-base font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-50 transition-colors"
              >
                Wyloguj się
              </button>
            </div>
          </div>
        </template>
        <template v-else>
          <div class="px-4">
            <RouterLink to="/login" @click="emit('close')" class="block w-full">
              <PrimaryButton class="w-full justify-center">Zaloguj się</PrimaryButton>
            </RouterLink>
          </div>
        </template>
      </div>
    </div>
  </div>
</template>
