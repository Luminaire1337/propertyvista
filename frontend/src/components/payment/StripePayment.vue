<script setup lang="ts">
import { useCreatePaymentIntentMutation } from '@/mutations/payment'
import { onMounted } from 'vue'
import {
  VueStripeProvider,
  VueStripeElements,
  VueStripePaymentElement,
} from '@vue-stripe/vue-stripe'
import StripeConfirmButton from './StripeConfirmButton.vue'

const props = defineProps<{
  propertyPoints: number
}>()

const publishableKey = import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY
const { data, isError, isPending, mutate } = useCreatePaymentIntentMutation()

onMounted(() => {
  mutate({ propertyPoints: props.propertyPoints })
})
</script>

<template>
  <!-- Skeleton loader -->
  <div v-if="isPending" class="w-full max-w-md space-y-4">
    <!-- Payment option 1 -->
    <div class="h-16 bg-gray-200 rounded-lg animate-pulse"></div>
    <!-- Payment option 2 -->
    <div class="h-16 bg-gray-200 rounded-lg animate-pulse"></div>
    <!-- Submit button -->
    <div class="h-12 bg-gray-300 rounded-lg animate-pulse mt-6"></div>
  </div>

  <!-- Error state -->
  <div v-else-if="isError || !data" class="text-center py-12">
    <h2 class="text-2xl font-bold text-gray-900">Wystąpił błąd</h2>
    <p class="text-gray-600 mt-2">
      Nie udało się załadować formularza płatności. Proszę spróbować ponownie później.
    </p>
  </div>

  <VueStripeProvider v-else :publishable-key="publishableKey">
    <VueStripeElements :client-secret="data.clientSecret">
      <VueStripePaymentElement />
      <StripeConfirmButton class="mt-6 w-full" />
    </VueStripeElements>
  </VueStripeProvider>
</template>
