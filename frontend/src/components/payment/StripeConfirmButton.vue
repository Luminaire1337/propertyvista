<script setup lang="ts">
import { useStripe, useStripeElements } from '@vue-stripe/vue-stripe'
import PrimaryButton from '@/components/PrimaryButton.vue'

const { stripe } = useStripe()
const { elements } = useStripeElements()

const handleSubmit = async () => {
  if (!stripe.value || !elements.value) {
    return
  }

  const { error } = await stripe.value.confirmPayment({
    elements: elements.value,
    confirmParams: {
      return_url: `${window.location.origin}/property-points/complete`,
    },
  })

  if (error) {
    console.error(error.message)
  }
}
</script>

<template>
  <PrimaryButton @click="handleSubmit">Zapłać</PrimaryButton>
</template>
