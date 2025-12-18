<script setup lang="ts">
import { useVerifyEmailMutation } from '@/mutations/user'
import router from '@/router'
import { useRoute } from 'vue-router'
import { toast } from 'vue-sonner'

const route = useRoute()
const token = route.query.token as string | undefined

if (token) {
  const verifyEmailMutation = useVerifyEmailMutation()
  verifyEmailMutation.mutate({ token })
} else {
  toast.error('Brak tokenu weryfikacyjnego w URL.')
  router.push({ name: 'home' })
}
</script>

<template>
  <div class="flex flex-col items-center justify-center min-h-[60vh] text-center px-4">
    <h1 class="text-4xl font-bold mb-4">Proszę czekać na weryfikację adresu email...</h1>
  </div>
</template>
