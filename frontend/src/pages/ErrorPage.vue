<script setup lang="ts">
import PrimaryButton from '@/components/PrimaryButton.vue'
</script>

<template>
  <!-- Simple error page -->
  <div class="grow flex flex-col items-center justify-center text-center px-4">
    <h1 class="text-4xl font-bold mb-4">Coś poszło nie tak</h1>
    <p class="text-lg mb-6">
      Przepraszamy, ale wystąpił błąd podczas ładowania strony. Prosimy spróbować ponownie później.
    </p>
    <RouterLink to="/">
      <PrimaryButton>Powrót do strony głównej</PrimaryButton>
    </RouterLink>
  </div>
</template>
