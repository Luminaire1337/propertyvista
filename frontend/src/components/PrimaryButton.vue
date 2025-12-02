<script setup lang="ts">
defineProps<{
  type?: 'button' | 'submit' | 'reset'
  customClass?: string
}>()
</script>

<template>
  <button
    :class="[
      'bg-primary text-white px-4 py-2 rounded hover:bg-primary-dark transition-colors',
      customClass,
    ]"
    :type="type || 'button'"
  >
    <slot />
  </button>
</template>
