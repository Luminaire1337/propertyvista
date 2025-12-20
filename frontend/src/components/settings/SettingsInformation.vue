<script setup lang="ts">
import { ref, watch } from 'vue'
import useCurrentUser from '@/queries/useCurrentUser'
import { useUpdateUserInfoMutation } from '@/mutations/user'
import PrimaryButton from '@/components/PrimaryButton.vue'

const { data: user } = useCurrentUser()
const updateInfoMutation = useUpdateUserInfoMutation()

const firstName = ref('')
const lastName = ref('')
const phoneNumber = ref('')

watch(
  user,
  (newUser) => {
    if (newUser) {
      firstName.value = newUser.firstName || ''
      lastName.value = newUser.lastName || ''
      phoneNumber.value = newUser.phoneNumber || ''
    }
  },
  { immediate: true },
)

const handleSubmit = (event: Event) => {
  event.preventDefault()
  updateInfoMutation.mutate({
    id: 'me',
    infoData: {
      firstName: firstName.value,
      lastName: lastName.value,
      phoneNumber: phoneNumber.value,
    },
  })
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-4">Informacje osobiste</h2>
    <form @submit="handleSubmit" class="space-y-4">
      <div>
        <label for="firstName" class="block text-gray-700 mb-2">Imię:</label>
        <input
          v-model="firstName"
          type="text"
          id="firstName"
          required
          class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
        />
      </div>
      <div>
        <label for="lastName" class="block text-gray-700 mb-2">Nazwisko:</label>
        <input
          v-model="lastName"
          type="text"
          id="lastName"
          required
          class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
        />
      </div>
      <div>
        <label for="phoneNumber" class="block text-gray-700 mb-2">Numer telefonu:</label>
        <input
          v-model="phoneNumber"
          type="tel"
          id="phoneNumber"
          required
          class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
        />
      </div>
      <PrimaryButton
        type="submit"
        :disabled="updateInfoMutation.isPending.value"
        custom-class="w-full"
      >
        {{ updateInfoMutation.isPending.value ? 'Zapisywanie...' : 'Zapisz zmiany' }}
      </PrimaryButton>
    </form>
  </div>
</template>
