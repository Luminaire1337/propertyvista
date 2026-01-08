<script setup lang="ts">
import { ref, computed, onBeforeUnmount } from 'vue'
import useCurrentUser from '@/queries/useCurrentUser'
import { useUpdateUserAvatarMutation, useDeleteUserAvatarMutation } from '@/mutations/user'
import PrimaryButton from '@/components/PrimaryButton.vue'
import AvatarImage from '@/components/AvatarImage.vue'
import ConfirmationModal from '@/components/ConfirmationModal.vue'

const { data: user } = useCurrentUser()
const updateAvatarMutation = useUpdateUserAvatarMutation()
const deleteAvatarMutation = useDeleteUserAvatarMutation()

const fileInput = ref<HTMLInputElement | null>(null)
const selectedFile = ref<File | null>(null)
const previewUrl = ref<string | null>(null)
const isDeleteModalOpen = ref(false)

const currentAvatarUrl = computed(() => previewUrl.value || user.value?.avatarImagePath)

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]

  if (file) {
    // Revoke old preview URL if it exists to prevent memory leak
    if (previewUrl.value) {
      URL.revokeObjectURL(previewUrl.value)
    }
    selectedFile.value = file
    previewUrl.value = URL.createObjectURL(file)
  }
}

const handleUpload = () => {
  if (selectedFile.value) {
    updateAvatarMutation.mutate(
      { avatarImage: selectedFile.value },
      {
        onSuccess: () => {
          // Clean up object URL
          if (previewUrl.value) {
            URL.revokeObjectURL(previewUrl.value)
          }
          selectedFile.value = null
          previewUrl.value = null
          if (fileInput.value) {
            fileInput.value.value = ''
          }
        },
      },
    )
  }
}

// Cleanup: Revoke object URL when component unmounts to prevent memory leak
onBeforeUnmount(() => {
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
  }
})

const openDeleteModal = () => {
  isDeleteModalOpen.value = true
}

const closeDeleteModal = () => {
  isDeleteModalOpen.value = false
}

const handleDelete = () => {
  deleteAvatarMutation.mutate()
  closeDeleteModal()
}

const triggerFileInput = () => {
  fileInput.value?.click()
}
</script>

<template>
  <div>
    <h2 class="text-2xl font-semibold mb-6">Awatar</h2>

    <!-- Current Avatar Section -->
    <div class="mb-8">
      <div class="bg-gray-50 border border-gray-200 rounded-lg p-6 mb-4">
        <div class="flex items-center space-x-6">
          <AvatarImage
            v-if="user"
            :src="currentAvatarUrl ?? undefined"
            :size="96"
            alt="Awatar użytkownika"
            class="border-2 border-gray-300 rounded-full shadow-sm"
          />
          <div class="flex-1">
            <p class="text-sm text-gray-600 mb-3">
              {{ previewUrl ? 'Nowy awatar' : 'Obecny awatar' }}
            </p>
            <button
              v-if="user?.avatarImagePath && !previewUrl"
              @click="openDeleteModal"
              type="button"
              class="text-red-600 hover:text-red-800 text-sm font-medium underline"
            >
              Usuń awatar
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Upload New Avatar Section -->
    <div>
      <h3 class="text-lg font-semibold mb-4">
        {{ selectedFile ? 'Zmień wybór' : 'Prześlij nowy awatar' }}
      </h3>

      <!-- Info Notice -->
      <div class="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-4">
        <p class="text-sm text-blue-800">
          <strong>Informacja:</strong> Po przesłaniu nowego awatara, jest on przekazywany do
          zewnętrznej usługi do przetwarzania obrazów. Weryfikacja może potrwać kilka minut.
        </p>
      </div>

      <form @submit.prevent="handleUpload" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2"> Wybierz plik </label>
          <input
            ref="fileInput"
            type="file"
            accept="image/png, image/jpeg"
            @change="handleFileSelect"
            class="hidden"
          />
          <button
            @click="triggerFileInput"
            type="button"
            class="w-full px-4 py-3 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors text-left flex items-center justify-between"
          >
            <span :class="selectedFile ? 'text-gray-900 font-medium' : 'text-gray-500'">
              {{ selectedFile ? selectedFile.name : 'Kliknij, aby wybrać plik' }}
            </span>
            <span class="text-xs text-gray-500">PNG, JPG</span>
          </button>
          <p class="text-xs text-gray-500 mt-2">
            Zalecany rozmiar: 400x400px. Maksymalny rozmiar pliku: 5MB
          </p>
        </div>
        <PrimaryButton
          type="submit"
          :disabled="!selectedFile || updateAvatarMutation.isPending.value"
          custom-class="w-full"
        >
          {{ updateAvatarMutation.isPending.value ? 'Przesyłanie...' : 'Prześlij awatar' }}
        </PrimaryButton>
      </form>
    </div>

    <!-- Delete Avatar Modal -->
    <ConfirmationModal
      :is-open="isDeleteModalOpen"
      title="Usuń awatar"
      description="Czy na pewno chcesz usunąć swój awatar? Zostanie zastąpiony domyślnym awatarem."
      :confirm-text="deleteAvatarMutation.isPending.value ? 'Usuwanie...' : 'Usuń awatar'"
      cancel-text="Anuluj"
      :is-pending="deleteAvatarMutation.isPending.value"
      variant="danger"
      @close="closeDeleteModal"
      @confirm="handleDelete"
    />
  </div>
</template>
