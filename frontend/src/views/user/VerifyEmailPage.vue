<script setup lang="ts">
import { useVerifyEmailMutation } from '@/mutations/user'
import router from '@/router'
import { useRoute } from 'vue-router'
import { toast } from 'vue-sonner'
import { computed } from 'vue'

const route = useRoute()
const token = computed(() => route.query.token as string)

if (token.value) {
  const verifyEmailMutation = useVerifyEmailMutation()
  verifyEmailMutation.mutate({ token: token.value })
} else {
  toast.error('Brak tokenu weryfikacyjnego w URL.')
  router.push({ name: 'home' })
}
</script>

<template>
  <div class="grow flex flex-col items-center justify-center text-center px-4">
    <h1 class="text-4xl font-bold mb-4">Proszę czekać na weryfikację adresu email...</h1>
  </div>
</template>
