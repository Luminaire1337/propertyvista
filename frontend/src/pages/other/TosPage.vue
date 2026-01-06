<script setup lang="ts">
import tosContent from '@/assets/txt/tos.txt?raw'
</script>

<template>
  <div class="grow flex flex-col items-center justify-center text-center px-4 py-8">
    <div class="max-w-4xl w-full bg-white p-6 rounded shadow-md text-left whitespace-pre-wrap">
      <h1 class="text-4xl font-bold mb-4 text-center">Regulamin</h1>
      <pre class="whitespace-pre-wrap">{{ tosContent }}</pre>
    </div>
  </div>
</template>
