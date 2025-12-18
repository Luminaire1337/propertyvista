<script setup lang="ts">
import privacyPolicyContent from '@/assets/txt/privacy-policy.txt?raw'
</script>

<template>
  <div class="flex flex-col items-center justify-center min-h-[60vh] text-center px-4">
    <div class="max-w-4xl w-full bg-white p-6 rounded shadow-md text-left whitespace-pre-wrap">
      <h1 class="text-4xl font-bold mb-4 text-center">Polityka Prywatności</h1>
      <pre class="whitespace-pre-wrap">{{ privacyPolicyContent }}</pre>
    </div>
  </div>
</template>
