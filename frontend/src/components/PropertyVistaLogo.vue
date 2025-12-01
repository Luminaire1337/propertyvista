<script setup lang="ts">
import { House } from 'lucide-vue-next'

defineProps<{
  size?: number
}>()
</script>

<template>
  <div
    :style="{ width: size ? size + 'px' : '32px', height: size ? size + 'px' : '32px' }"
    class="bg-primary rounded flex items-center justify-center"
  >
    <House class="text-white" :size="size ? size * 0.6 : 20" />
  </div>
</template>
