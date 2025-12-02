<script setup lang="ts">
import type { useAuthStore } from '@/stores/auth'
import { Menu, MenuButton, MenuItems, MenuItem } from '@headlessui/vue'
import { ChevronDown } from 'lucide-vue-next'
import AvatarImage from '../AvatarImage.vue'
import { toast } from 'vue-sonner'
import router from '@/router'

const props = defineProps<{
  authStore: ReturnType<typeof useAuthStore>
}>()

const handleLogout = (event: Event) => {
  event.preventDefault()
  toast.promise(props.authStore.logout(), {
    loading: 'Wylogowywanie...',
    success: () => {
      router.push({ name: 'home' })
      return 'Pomyślnie wylogowano.'
    },
    error: (err: Error) => err.message,
  })
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
      <AvatarImage
        :src="authStore.userProfile!.avatarImagePath"
        alt="Awatar użytkownika"
        :size="32"
      />
      <span>Witaj, {{ authStore.userProfile!.firstName }}!</span>
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
            <template #default="{ active }">
              <template v-if="link.path">
                <RouterLink
                  :to="link.path"
                  :class="[
                    active ? 'bg-gray-200' : '',
                    'block px-4 py-2 text-gray-800 w-full transition-colors',
                  ]"
                >
                  {{ link.name }}
                </RouterLink>
              </template>
              <template v-else-if="link.action">
                <button
                  @click="link.action"
                  :class="[
                    active ? 'bg-gray-200' : '',
                    'w-full text-left block px-4 py-2 text-gray-800 transition-colors',
                  ]"
                >
                  {{ link.name }}
                </button>
              </template>
            </template>
          </MenuItem>
        </div>
      </MenuItems>
    </transition>
  </Menu>
</template>
