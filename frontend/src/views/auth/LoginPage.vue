<script setup lang="ts">
import { ref } from 'vue'
import { useLoginMutation } from '@/mutations/auth'
import PrimaryButton from '@/components/PrimaryButton.vue'

const loginMutation = useLoginMutation()

const email = ref('')
const password = ref('')

const handleLogin = (event: Event) => {
  event.preventDefault()
  loginMutation.mutate({ email: email.value, password: password.value })
}
</script>

<template>
  <div class="grow flex flex-col items-center justify-center text-center px-4">
    <h1 class="text-4xl font-bold mb-4">Zaloguj się</h1>
    <form @submit="handleLogin" class="w-full max-w-sm bg-white p-6 rounded shadow-md">
      <div class="mb-4 text-left">
        <label for="email" class="block text-gray-700 mb-2">E-mail:</label>
        <input
          v-model="email"
          type="email"
          id="email"
          required
          class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
        />
      </div>
      <div class="mb-6 text-left">
        <label for="password" class="block text-gray-700 mb-2">Hasło:</label>
        <input
          v-model="password"
          type="password"
          id="password"
          required
          class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
        />
      </div>
      <PrimaryButton type="submit" class="w-full" :disabled="loginMutation.isPending.value">
        {{ loginMutation.isPending.value ? 'Logowanie...' : 'Zaloguj się' }}
      </PrimaryButton>
      <RouterLink to="/register" class="block mt-4 text-sm text-gray-900 hover:underline">
        Nie masz konta? Zarejestruj się
      </RouterLink>
    </form>
  </div>
</template>
