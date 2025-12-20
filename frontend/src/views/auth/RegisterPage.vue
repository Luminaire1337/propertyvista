<script setup lang="ts">
import { ref } from 'vue'
import { useRegisterMutation } from '@/mutations/auth'
import PrimaryButton from '@/components/PrimaryButton.vue'

const registerMutation = useRegisterMutation()

const page = ref<'register' | 'email-verification'>('register')
const email = ref('')
const password = ref('')
const firstName = ref('')
const lastName = ref('')
const phoneNumber = ref('')

const handleRegister = (event: Event) => {
  event.preventDefault()
  registerMutation.mutate(
    {
      email: email.value,
      password: password.value,
      firstName: firstName.value,
      lastName: lastName.value,
      phoneNumber: phoneNumber.value,
    },
    {
      onSuccess: () => {
        page.value = 'email-verification'
      },
    },
  )
}
</script>

<template>
  <div class="flex flex-col items-center justify-center min-h-[60vh] text-center px-4">
    <template v-if="page === 'email-verification'">
      <h1 class="text-4xl font-bold mb-4">Weryfikacja e-mail</h1>
      <p class="text-lg mb-6 max-w-2xl">
        Dziękujemy za rejestrację! Na podany adres e-mail
        <strong>{{ email }}</strong> wysłaliśmy wiadomość z linkiem weryfikacyjnym. Prosimy
        sprawdzić swoją skrzynkę pocztową i kliknąć w link, aby aktywować konto.
      </p>
      <RouterLink to="/login">
        <PrimaryButton>Przejdź do strony logowania</PrimaryButton>
      </RouterLink>
      <RouterLink to="/" class="mt-4 text-sm text-gray-900 hover:underline">
        Powrót do strony głównej
      </RouterLink>
    </template>
    <template v-else>
      <h1 class="text-4xl font-bold mb-4">Zarejestruj się</h1>
      <form @submit="handleRegister" class="w-full max-w-sm bg-white p-6 rounded shadow-md">
        <div class="mb-4 text-left">
          <label for="firstName" class="block text-gray-700 mb-2">Imię:</label>
          <input
            v-model="firstName"
            type="text"
            id="firstName"
            required
            class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
          />
        </div>
        <div class="mb-4 text-left">
          <label for="lastName" class="block text-gray-700 mb-2">Nazwisko:</label>
          <input
            v-model="lastName"
            type="text"
            id="lastName"
            required
            class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
          />
        </div>
        <div class="mb-4 text-left">
          <label for="phoneNumber" class="block text-gray-700 mb-2">Numer telefonu:</label>
          <input
            v-model="phoneNumber"
            type="tel"
            id="phoneNumber"
            required
            class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
          />
        </div>
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
        <div class="mb-4 text-left">
          <label for="password" class="block text-gray-700 mb-2">Hasło:</label>
          <input
            v-model="password"
            type="password"
            id="password"
            required
            class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
          />
        </div>
        <div class="mb-4 text-sm text-gray-600">
          Rejestrując się, zgadzasz się na naszą
          <RouterLink to="/terms-of-service" class="text-primary hover:underline"
            >Politykę Prywatności</RouterLink
          >
          oraz
          <RouterLink to="/privacy-policy" class="text-primary hover:underline"
            >Regulamin</RouterLink
          >.
        </div>
        <PrimaryButton type="submit" class="w-full" :disabled="registerMutation.isPending.value">
          {{ registerMutation.isPending.value ? 'Rejestrowanie...' : 'Zarejestruj się' }}
        </PrimaryButton>
        <RouterLink to="/login" class="block mt-4 text-sm text-gray-900 hover:underline">
          Masz już konto? Zaloguj się
        </RouterLink>
      </form>
    </template>
  </div>
</template>
