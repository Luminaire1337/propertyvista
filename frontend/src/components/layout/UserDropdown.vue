<script setup lang="ts">
import useCurrentUser from '@/queries/useCurrentUser'
import { useLogoutMutation } from '@/mutations/auth'
import { Menu, MenuButton, MenuItems, MenuItem } from '@headlessui/vue'
import { ChevronDown } from 'lucide-vue-next'
import AvatarImage from '../AvatarImage.vue'

const { data: user } = useCurrentUser()
const logoutMutation = useLogoutMutation()

const handleLogout = (event: Event) => {
  event.preventDefault()
  logoutMutation.mutate()
}

const dropDownLinks = [
  {
    name: 'Moje ogłoszenia',
    path: '/my-listings',
  },
  {
    name: 'Ustawienia konta',
    path: '/settings',
  },
  {
    name: 'Wyloguj się',
    action: handleLogout,
  },
]
</script>

<template>
  <Menu as="div" class="relative inline-block text-left">
    <MenuButton
      class="px-4 py-2 rounded flex items-center space-x-2 hover:bg-gray-200 transition-colors backdrop-blur-sm"
    >
      <AvatarImage :src="user?.avatarImagePath ?? undefined" alt="Awatar użytkownika" :size="32" />
      <span>Witaj, {{ user?.firstName }}!</span>
      <ChevronDown :size="16" />
    </MenuButton>
    <transition
      enter-active-class="transition ease-out duration-100"
      enter-from-class="transform opacity-0 scale-95"
      enter-to-class="transform opacity-100 scale-100"
      leave-active-class="transition ease-in duration-75"
      leave-from-class="transform opacity-100 scale-100"
      leave-to-class="transform opacity-0 scale-95"
    >
      <MenuItems class="absolute z-20 right-0 mt-2 w-56 rounded bg-gray-100 backdrop-blur-sm">
        <div class="py-1">
          <MenuItem v-for="link in dropDownLinks" :key="link.name">
            <RouterLink
              v-if="link.path"
              :to="link.path"
              class="block px-4 py-2 text-gray-800 w-full transition-colors ui-active:bg-gray-200 hover:bg-gray-200"
            >
              {{ link.name }}
            </RouterLink>
            <button
              v-else-if="link.action"
              @click="link.action"
              class="w-full text-left block px-4 py-2 text-gray-800 transition-colors ui-active:bg-gray-200 hover:bg-gray-200"
            >
              {{ link.name }}
            </button>
          </MenuItem>
        </div>
      </MenuItems>
    </transition>
  </Menu>
</template>
