<script setup lang="ts">
import { ref } from 'vue'
import { useUpdateUserPasswordMutation } from '@/mutations/user'
import PrimaryButton from '@/components/PrimaryButton.vue'
import { toast } from 'vue-sonner'

const updatePasswordMutation = useUpdateUserPasswordMutation()

const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const handleSubmit = (event: Event) => {
  event.preventDefault()

  if (newPassword.value !== confirmPassword.value) {
    toast.error('Nowe hasła nie są identyczne')
    return
  }

  if (newPassword.value.length < 8) {
    toast.error('Nowe hasło musi mieć co najmniej 8 znaków')
    return
  }

  updatePasswordMutation.mutate(
    {
      id: 'me',
      passwordData: {
        password: newPassword.value,
      },
    },
    {
      onSuccess: () => {
        currentPassword.value = ''
        newPassword.value = ''
        confirmPassword.value = ''
      },
    },
  )
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-4">Zmiana hasła</h2>
    <form @submit="handleSubmit" class="space-y-4">
      <div>
        <label for="currentPassword" class="block text-gray-700 mb-2">Obecne hasło:</label>
        <input
          v-model="currentPassword"
          type="password"
          id="currentPassword"
          required
          class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
        />
      </div>
      <div>
        <label for="newPassword" class="block text-gray-700 mb-2">Nowe hasło:</label>
        <input
          v-model="newPassword"
          type="password"
          id="newPassword"
          required
          minlength="8"
          class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
        />
      </div>
      <div>
        <label for="confirmPassword" class="block text-gray-700 mb-2">Potwierdź nowe hasło:</label>
        <input
          v-model="confirmPassword"
          type="password"
          id="confirmPassword"
          required
          minlength="8"
          class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
        />
      </div>
      <PrimaryButton
        type="submit"
        :disabled="updatePasswordMutation.isPending.value"
        custom-class="w-full"
      >
        {{ updatePasswordMutation.isPending.value ? 'Zapisywanie...' : 'Zmień hasło' }}
      </PrimaryButton>
    </form>
  </div>
</template>
