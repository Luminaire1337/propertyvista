<script setup lang="ts">
import { useCreatePropertyMutation } from '@/mutations/property'
import PropertyForm from '@/components/property/PropertyForm.vue'
import type { CreatePropertyRequest, UpdatePropertyRequest } from '@/services/property'

const createPropertyMutation = useCreatePropertyMutation()

const handleSubmit = (createData: CreatePropertyRequest | UpdatePropertyRequest) => {
  createPropertyMutation.mutate(createData as CreatePropertyRequest)
}
</script>

<template>
  <div class="grow flex flex-col items-center px-4 py-8">
    <div class="w-full max-w-4xl">
      <h1 class="text-4xl font-bold text-gray-900 mb-2 text-center">Dodaj nowe ogłoszenie</h1>

      <PropertyForm
        :mutation="createPropertyMutation"
        submit-text="Dodaj ogłoszenie"
        mode="create"
        @submit="handleSubmit"
      />
    </div>
  </div>
</template>
