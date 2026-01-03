<script setup lang="ts">
import PropertySearchForm from '@/components/PropertySearchForm.vue'
import router from '@/router'
import type { SearchFilters } from '@/services/property'
import type { LocationQueryRaw } from 'vue-router'

const handleSearch = (filters: SearchFilters) => {
  router.push({ path: '/properties', query: filters as LocationQueryRaw })
}
</script>

<template>
  <div class="container mx-auto px-4 py-12">
    <div class="text-center mb-12">
      <h1 class="text-4xl font-bold text-gray-900 mb-4">Znajdź swoją wymarzoną nieruchomość</h1>
      <p class="text-xl text-gray-600">
        Wyszukaj spośród tysięcy ofert nieruchomości w całej Polsce
      </p>
    </div>

    <PropertySearchForm @search="handleSearch" />
  </div>
</template>
