<script setup lang="ts">
import { User } from 'lucide-vue-next'

defineProps<{
  src?: string
  alt?: string
  size?: number
}>()
</script>

<template>
  <div
    :style="{
      width: size ? size + 'px' : '32px',
      height: size ? size + 'px' : '32px',
      borderRadius: '50%',
      overflow: 'hidden',
    }"
    class="bg-gray-300 flex items-center justify-center"
  >
    <img
      v-if="src"
      :src="src"
      :alt="alt || 'Avatar'"
      class="w-full h-full object-cover"
      loading="lazy"
    />
    <User v-else class="text-gray-400" :size="size ? size * 0.6 : 20" />
  </div>
</template>
