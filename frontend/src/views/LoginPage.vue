<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const email = ref('')
const password = ref('')

const handleSubmit = async () => {
  try {
    await authStore.login({ email: email.value, password: password.value })
    // Redirect to home page or dashboard after successful login
  } catch (error) {
    alert((error as Error).message)
  }
}
</script>

<template>
  <!-- TEMPORARY LOGIN FORM -->
  <form class="max-w-md mx-auto mt-10" @submit.prevent="handleSubmit">
    <h1 class="text-3xl font-bold mb-6">Login</h1>
    <div class="mb-4">
      <label class="block text-gray-700 text-sm font-bold mb-2" for="email">Email</label>
      <input
        v-model="email"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
        id="email"
        type="email"
        placeholder="Enter your email"
        required
      />
    </div>
    <div class="mb-6">
      <label class="block text-gray-700 text-sm font-bold mb-2" for="password">Password</label>
      <input
        v-model="password"
        class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
        id="password"
        type="password"
        placeholder="Enter your password"
        required
      />
    </div>
    <div class="flex items-center justify-between">
      <button
        class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline"
        type="submit"
      >
        Login
      </button>
    </div>
  </form>
</template>
