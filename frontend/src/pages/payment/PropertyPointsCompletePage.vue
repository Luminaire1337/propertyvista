<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { CheckCircle, XCircle } from 'lucide-vue-next'
import PrimaryButton from '@/components/PrimaryButton.vue'
import { useInvalidateCurrentUserQuery } from '@/mutations/user'

const route = useRoute()
const redirectStatus = computed(() => route.query.redirect_status as string)

const isSuccess = computed(() => redirectStatus.value === 'succeeded')

// Invalidate current user profile to refetch updated property points
const invalidateCurrentUser = useInvalidateCurrentUserQuery()
if (isSuccess.value) {
  invalidateCurrentUser.mutate()
}
</script>

<template>
  <div class="grow flex flex-col items-center justify-center text-center px-4">
    <div v-if="isSuccess" class="max-w-md">
      <div class="mb-6">
        <CheckCircle class="mx-auto h-16 w-16 text-green-600" />
      </div>
      <h1 class="text-4xl font-bold text-gray-900 mb-4">Płatność zakończona sukcesem!</h1>
      <p class="text-lg text-gray-600 mb-6">Twoje Property Points zostały dodane do konta.</p>
      <RouterLink to="/">
        <PrimaryButton>Powrót do strony głównej</PrimaryButton>
      </RouterLink>
    </div>

    <div v-else class="max-w-md">
      <div class="mb-6">
        <XCircle class="mx-auto h-16 w-16 text-red-600" />
      </div>
      <h1 class="text-4xl font-bold text-gray-900 mb-4">Płatność nie powiodła się</h1>
      <p class="text-lg text-gray-600 mb-6">
        Niestety, nie udało się przetworzyć płatności. Nie pobrano żadnych środków. Możesz spróbować
        ponownie później.
      </p>
      <div class="flex flex-col sm:flex-row gap-3 justify-center">
        <RouterLink to="/property-points">
          <PrimaryButton>Spróbuj ponownie</PrimaryButton>
        </RouterLink>
        <RouterLink to="/">
          <PrimaryButton>Powrót do strony głównej</PrimaryButton>
        </RouterLink>
      </div>
    </div>
  </div>
</template>
