<script setup lang="ts">
import client, { makeErrorResponseHumanReadable } from '@/api/client'
import type { components } from '@/api/generated/schema'
import router from '@/router'
import { useRoute } from 'vue-router'
import { toast } from 'vue-sonner'

const route = useRoute()
const token = route.query.token as string | undefined

type ErrorResponse = components['schemas']['ErrorResponse']
const verifyEmail = async (token: string) => {
  try {
    const { error } = await client.POST('/user/verify-email', {
      body: { token: token },
    })
    if (error) throw error
  } catch (err) {
    throw new Error(makeErrorResponseHumanReadable(err as ErrorResponse))
  }
}

toast.promise(verifyEmail(token || ''), {
  loading: 'Weryfikacja adresu email...',
  success: () => {
    router.push({ name: 'login' })
    return 'Pomyślnie zweryfikowano adres email.'
  },
  error: (err: Error) => err.message,
})
</script>

<template>
  <div class="flex flex-col items-center justify-center min-h-[60vh] text-center px-4">
    <h1 class="text-4xl font-bold mb-4">Proszę czekać na weryfikację adresu email...</h1>
  </div>
</template>
