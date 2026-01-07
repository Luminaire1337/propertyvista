<script setup lang="ts">
import PropertySearchForm from '@/components/PropertySearchForm.vue'
import { useRouter, type LocationQueryRaw } from 'vue-router'
import type { SearchFilters } from '@/services/property'

const router = useRouter()

const handleSearch = (filters: SearchFilters) => {
  router.push({ name: 'properties', query: filters as LocationQueryRaw })
}
</script>

<template>
  <div class="grow flex flex-col items-center justify-center text-center px-4">
    <div class="text-center mb-12">
      <h1 class="text-4xl font-bold text-gray-900 mb-4">Znajdź swoją wymarzoną nieruchomość</h1>
      <p class="text-xl text-gray-600">
        Wyszukaj spośród tysięcy ofert nieruchomości w całej Polsce
      </p>
    </div>

    <PropertySearchForm @search="handleSearch" />
  </div>
</template>
