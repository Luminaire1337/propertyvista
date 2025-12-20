<script setup lang="ts">
import { ref } from 'vue'
import useCurrentUser from '@/queries/useCurrentUser'
import { useUpdateUserEmailMutation, useDeleteUserMutation } from '@/mutations/user'
import PrimaryButton from '@/components/PrimaryButton.vue'
import ConfirmationModal from '@/components/ConfirmationModal.vue'

const { data: user } = useCurrentUser()
const updateEmailMutation = useUpdateUserEmailMutation()
const deleteUserMutation = useDeleteUserMutation()

const newEmail = ref('')
const isDeleteModalOpen = ref(false)
const deleteConfirmText = ref('')

const handleEmailUpdate = (event: Event) => {
  event.preventDefault()
  updateEmailMutation.mutate(
    {
      id: 'me',
      emailData: {
        email: newEmail.value,
      },
    },
    {
      onSuccess: () => {
        newEmail.value = ''
      },
    },
  )
}

const openDeleteModal = () => {
  isDeleteModalOpen.value = true
  deleteConfirmText.value = ''
}

const closeDeleteModal = () => {
  isDeleteModalOpen.value = false
  deleteConfirmText.value = ''
}

const handleDeleteAccount = () => {
  if (deleteConfirmText.value === 'USUŃ KONTO') {
    deleteUserMutation.mutate('me')
    closeDeleteModal()
  }
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-6">Prywatność i bezpieczeństwo</h2>

    <!-- Email Section -->
    <div class="mb-8">
      <h3 class="text-lg font-semibold mb-4">Adres e-mail</h3>
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-4 mb-4">
        <p class="text-sm text-gray-600">Obecny e-mail</p>
        <p class="font-medium text-gray-900 mt-1">{{ user?.email }}</p>
      </div>
      <form @submit="handleEmailUpdate" class="space-y-4">
        <div>
          <label for="newEmail" class="block text-sm font-medium text-gray-700 mb-2">
            Nowy adres e-mail
          </label>
          <input
            v-model="newEmail"
            type="email"
            id="newEmail"
            required
            placeholder="przykład@domena.pl"
            class="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
          />
        </div>
        <PrimaryButton
          type="submit"
          :disabled="updateEmailMutation.isPending.value || !newEmail"
          custom-class="w-full"
        >
          {{ updateEmailMutation.isPending.value ? 'Zapisywanie...' : 'Zmień e-mail' }}
        </PrimaryButton>
      </form>
    </div>

    <!-- Delete Account Section -->
    <div class="pt-6 border-t border-gray-200">
      <h3 class="text-lg font-semibold mb-4 text-red-600">Usuń konto</h3>
      <div class="bg-red-50 border border-red-200 rounded-lg p-4 mb-4">
        <p class="text-sm text-red-800">
          <strong>Ostrzeżenie:</strong> Ta operacja jest nieodwracalna. Wszystkie twoje dane, w tym
          ogłoszenia, zostaną trwale usunięte z systemu.
        </p>
      </div>
      <button
        @click="openDeleteModal"
        type="button"
        class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition-colors font-medium"
      >
        Usuń konto
      </button>
    </div>

    <!-- Delete Account Modal -->
    <ConfirmationModal
      :is-open="isDeleteModalOpen"
      title="Usuń konto"
      description="Ta operacja jest nieodwracalna. Wszystkie twoje dane, w tym ogłoszenia, zostaną trwale usunięte."
      :confirm-text="deleteUserMutation.isPending.value ? 'Usuwanie...' : 'Usuń konto'"
      cancel-text="Anuluj"
      :is-pending="deleteUserMutation.isPending.value || deleteConfirmText !== 'USUŃ KONTO'"
      variant="danger"
      @close="closeDeleteModal"
      @confirm="handleDeleteAccount"
    >
      <template #content>
        <div>
          <p class="text-sm font-medium mb-2">
            Aby potwierdzić, wpisz:
            <span class="font-mono bg-gray-100 px-2 py-1 rounded">USUŃ KONTO</span>
          </p>
          <input
            v-model="deleteConfirmText"
            type="text"
            placeholder="Wpisz USUŃ KONTO"
            class="w-full px-3 py-2 border rounded focus:outline-none focus:ring focus:border-red-300"
            @keyup.enter="handleDeleteAccount"
          />
        </div>
      </template>
    </ConfirmationModal>
  </div>
</template>
