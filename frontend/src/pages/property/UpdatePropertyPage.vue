<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  usePartiallyUpdatePropertyMutation,
  type PartialUpdatePropertyMutationParameters,
} from '@/mutations/property'
import PropertyForm from '@/components/PropertyForm.vue'
import type { CreatePropertyRequest, UpdatePropertyRequest } from '@/services/property'
import usePropertyDetails from '@/queries/usePropertyDetails'
import useCurrentUser from '@/queries/useCurrentUser'
import PrimaryButton from '@/components/PrimaryButton.vue'

const route = useRoute()
const slug = computed(() => route.params.slug as string)

const { data: property, isPending } = usePropertyDetails(slug)
const { data: currentUser } = useCurrentUser()
const updatePropertyMutation = usePartiallyUpdatePropertyMutation()

// Check if current user owns this property
const isOwner = computed(() => {
  if (!property.value || !currentUser.value) return false
  return property.value.user.id === currentUser.value.id
})

// Redirect if not the owner
const shouldShowUnauthorized = computed(() => {
  // Only show unauthorized if property loaded and user is not the owner
  return !isPending.value && property.value && !isOwner.value
})

const initialData = computed(() => property.value || undefined)

const handleSubmit = (updateData: CreatePropertyRequest | UpdatePropertyRequest) => {
  updatePropertyMutation.mutate({
    slug: slug.value,
    updateData: updateData as UpdatePropertyRequest,
  } as PartialUpdatePropertyMutationParameters)
}
</script>

<template>
  <div class="grow flex flex-col items-center px-4 py-8">
    <div class="w-full max-w-4xl">
      <h1 class="text-4xl font-bold text-gray-900 mb-2 text-center">Edytuj ogłoszenie</h1>

      <!-- Skeleton Loading State -->
      <div v-if="isPending" class="space-y-8 mt-8">
        <!-- Image Upload Section Skeleton -->
        <div class="bg-white rounded shadow-md p-6">
          <div class="h-6 bg-gray-300 rounded w-1/3 mb-4 animate-pulse"></div>
          <div class="h-24 bg-gray-300 rounded mb-4 animate-pulse"></div>
          <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
            <div
              v-for="i in 4"
              :key="i"
              class="aspect-square bg-gray-300 rounded animate-pulse"
            ></div>
          </div>
        </div>

        <!-- Property Details Section Skeleton -->
        <div class="bg-white rounded shadow-md p-6">
          <div class="h-6 bg-gray-300 rounded w-1/3 mb-4 animate-pulse"></div>
          <div class="space-y-4">
            <div class="h-10 bg-gray-300 rounded animate-pulse"></div>
            <div class="h-32 bg-gray-300 rounded animate-pulse"></div>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div class="h-10 bg-gray-300 rounded animate-pulse"></div>
              <div class="h-10 bg-gray-300 rounded animate-pulse"></div>
            </div>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div class="h-10 bg-gray-300 rounded animate-pulse"></div>
              <div class="h-10 bg-gray-300 rounded animate-pulse"></div>
            </div>
          </div>
        </div>

        <!-- Property Points Section Skeleton -->
        <div class="bg-white rounded shadow-md p-6">
          <div class="h-6 bg-gray-300 rounded w-1/2 mb-4 animate-pulse"></div>
          <div class="h-10 bg-gray-300 rounded mb-4 animate-pulse"></div>
          <div class="h-20 bg-gray-300 rounded animate-pulse"></div>
        </div>

        <!-- Submit Button Skeleton -->
        <div class="flex justify-end">
          <div class="h-10 bg-gray-300 rounded w-40 animate-pulse"></div>
        </div>
      </div>

      <!-- Unauthorized Access -->
      <div
        v-else-if="shouldShowUnauthorized"
        class="bg-red-50 border border-red-200 rounded p-6 text-center mt-8"
      >
        <h2 class="text-xl font-bold text-red-900 mb-2">Brak uprawnień</h2>
        <p class="text-red-700 mb-4">Nie masz uprawnień do edycji tego ogłoszenia.</p>
        <RouterLink :to="{ name: 'user-properties' }">
          <PrimaryButton> Przejdź do swoich ogłoszeń </PrimaryButton>
        </RouterLink>
      </div>

      <!-- Property Form -->
      <PropertyForm
        v-else-if="property && isOwner"
        :initial-data="initialData"
        :mutation="updatePropertyMutation"
        submit-text="Zapisz zmiany"
        mode="edit"
        @submit="handleSubmit"
      />

      <!-- Error State -->
      <div v-else class="bg-red-50 border border-red-200 rounded p-4 text-center mt-8">
        <p class="text-red-700">Nie udało się załadować danych ogłoszenia.</p>
      </div>
    </div>
  </div>
</template>
