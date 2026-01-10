<script setup lang="ts">
import { ref, computed } from 'vue'
import PrimaryButton from '@/components/PrimaryButton.vue'
import StripePayment from '@/components/payment/StripePayment.vue'
import useCurrentPaymentRate from '@/queries/useCurrentPaymentRate'
import { formatPrice } from '@/utils'

const propertyPoints = ref(7)
const showPayment = ref(false)

const { data: paymentRate, isPending: isLoadingRate } = useCurrentPaymentRate()

const totalPrice = computed(() => {
  if (!paymentRate.value?.currentRate) return 0
  return propertyPoints.value * paymentRate.value.currentRate
})

const handleProceedToPayment = () => {
  if (propertyPoints.value < 1 || propertyPoints.value > 100) {
    return
  }
  showPayment.value = true
}

const handleCancel = () => {
  showPayment.value = false
}
</script>

<template>
  <div class="grow flex flex-col items-center justify-center text-center px-4">
    <div v-if="!showPayment" class="w-full max-w-md">
      <h1 class="text-4xl font-bold text-gray-900 mb-2">Kup Property Points</h1>

      <!-- Skeleton loader -->
      <div v-if="isLoadingRate" class="bg-white p-8 rounded-lg shadow-md">
        <div class="h-6 bg-gray-300 rounded w-1/2 mb-4 animate-pulse"></div>
        <div class="h-12 bg-gray-300 rounded mb-4 animate-pulse"></div>
        <div class="h-24 bg-gray-300 rounded mb-4 animate-pulse"></div>
        <div class="h-12 bg-gray-300 rounded animate-pulse"></div>
      </div>

      <div v-else class="bg-white p-8 rounded-lg shadow-md">
        <div class="mb-6 text-left">
          <label for="propertyPoints" class="block text-gray-700 font-medium mb-2">
            Liczba Property Points (1-100):
          </label>
          <input
            v-model.number="propertyPoints"
            type="number"
            id="propertyPoints"
            min="1"
            max="100"
            required
            class="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-transparent"
          />
          <p class="text-sm text-gray-500 mt-2">Możesz jednorazowo zakupić do 100 punktów.</p>
        </div>

        <div class="mb-6 p-4 bg-gray-50 rounded-lg">
          <div class="flex justify-between items-center mb-2">
            <span class="text-gray-700 font-medium">Punkty:</span>
            <span class="text-gray-900 font-semibold">{{ propertyPoints }}</span>
          </div>
          <div class="flex justify-between items-center">
            <span class="text-gray-700 font-medium">Cena za punkt:</span>
            <span class="text-gray-900 font-semibold">
              {{ paymentRate?.currentRate ? formatPrice(paymentRate.currentRate) : formatPrice(0) }}
            </span>
          </div>
          <div class="border-t border-gray-300 mt-3 pt-3">
            <div class="flex justify-between items-center">
              <span class="text-lg font-bold text-gray-900">Łącznie:</span>
              <span class="text-2xl font-bold text-green-600">
                {{ formatPrice(totalPrice) }}
              </span>
            </div>
          </div>
        </div>

        <PrimaryButton
          @click="handleProceedToPayment"
          class="w-full"
          :disabled="propertyPoints < 1 || propertyPoints > 100 || isLoadingRate"
        >
          Przejdź do płatności
        </PrimaryButton>
      </div>
    </div>

    <div v-else class="w-full max-w-md">
      <h1 class="text-4xl font-bold text-gray-900 mb-2">Finalizowanie płatności</h1>
      <p class="text-gray-600 mb-6">
        {{ propertyPoints }} Property Points - {{ formatPrice(totalPrice) }}
      </p>

      <StripePayment :property-points="propertyPoints" />

      <button @click="handleCancel" class="mt-4 text-gray-600 hover:text-gray-900 underline">
        Anuluj i wróć
      </button>
    </div>
  </div>
</template>
