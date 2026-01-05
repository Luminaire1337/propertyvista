<script setup lang="ts">
import { ref } from 'vue'
import { MapPin, Search } from 'lucide-vue-next'
import PrimaryButton from './PrimaryButton.vue'
import type { SearchFilters } from '@/services/property'

const props = defineProps<{
  initialFilters?: SearchFilters
}>()

const emit = defineEmits<{
  search: [filters: SearchFilters]
}>()

const filters = ref<SearchFilters>({ ...props.initialFilters })

const handleSearch = () => {
  // Remove undefined, null, or empty string values from filters
  const cleanedFilters = Object.fromEntries(
    Object.entries(filters.value).filter(
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      ([_, value]) => value !== undefined && value !== null && value !== '',
    ),
  ) as SearchFilters
  emit('search', cleanedFilters)
}

const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === 'Enter') {
    handleSearch()
  }
}
</script>

<template>
  <div class="w-full max-w-5xl mx-auto p-6 bg-white rounded shadow-lg">
    <h2 class="text-2xl font-bold mb-6 text-gray-800">Znajdź swoją nieruchomość</h2>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-6">
      <!-- City -->
      <div class="relative">
        <label for="city" class="block text-sm font-medium text-gray-700 mb-1"> Miasto </label>
        <div class="relative">
          <MapPin
            class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5"
          />
          <input
            id="city"
            v-model="filters.city"
            type="text"
            placeholder="np. Warszawa"
            class="w-full pl-10 pr-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
            @keydown="handleKeyDown"
          />
        </div>
      </div>

      <!-- Min Price -->
      <div>
        <label for="minPrice" class="block text-sm font-medium text-gray-700 mb-1">
          Cena min. (PLN)
        </label>
        <input
          id="minPrice"
          v-model.number="filters.minPrice"
          type="number"
          placeholder="np. 200 000"
          class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
          @keydown="handleKeyDown"
        />
      </div>

      <!-- Max Price -->
      <div>
        <label for="maxPrice" class="block text-sm font-medium text-gray-700 mb-1">
          Cena maks. (PLN)
        </label>
        <input
          id="maxPrice"
          v-model.number="filters.maxPrice"
          type="number"
          placeholder="np. 500 000"
          class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
          @keydown="handleKeyDown"
        />
      </div>

      <!-- Min Rooms -->
      <div>
        <label for="minRooms" class="block text-sm font-medium text-gray-700 mb-1">
          Pokoje min.
        </label>
        <input
          id="minRooms"
          v-model.number="filters.minRooms"
          type="number"
          min="0"
          placeholder="np. 2"
          class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
          @keydown="handleKeyDown"
        />
      </div>

      <!-- Max Rooms -->
      <div>
        <label for="maxRooms" class="block text-sm font-medium text-gray-700 mb-1">
          Pokoje maks.
        </label>
        <input
          id="maxRooms"
          v-model.number="filters.maxRooms"
          type="number"
          min="0"
          placeholder="np. 4"
          class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
          @keydown="handleKeyDown"
        />
      </div>

      <!-- Min Area -->
      <div>
        <label for="minArea" class="block text-sm font-medium text-gray-700 mb-1">
          Powierzchnia min. (m²)
        </label>
        <input
          id="minArea"
          v-model.number="filters.minArea"
          type="number"
          min="0"
          step="0.1"
          placeholder="np. 50"
          class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
          @keydown="handleKeyDown"
        />
      </div>

      <!-- Max Area -->
      <div>
        <label for="maxArea" class="block text-sm font-medium text-gray-700 mb-1">
          Powierzchnia maks. (m²)
        </label>
        <input
          id="maxArea"
          v-model.number="filters.maxArea"
          type="number"
          min="0"
          step="0.1"
          placeholder="np. 100"
          class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
          @keydown="handleKeyDown"
        />
      </div>

      <!-- Parking -->
      <div>
        <label for="parking" class="block text-sm font-medium text-gray-700 mb-1">
          Miejsce postojowe
        </label>
        <select
          id="parking"
          v-model="filters.parking"
          class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300 appearance-none bg-white"
        >
          <option :value="undefined">-</option>
          <option :value="true">Dostępne</option>
          <option :value="false">Brak</option>
        </select>
      </div>

      <!-- Search Button -->
      <div class="flex items-end">
        <PrimaryButton
          type="button"
          custom-class="w-full px-8 py-2 text-lg font-semibold inline-flex items-center justify-center gap-2"
          @click="handleSearch"
        >
          <Search class="w-5 h-5" />
          Szukaj nieruchomości
        </PrimaryButton>
      </div>
    </div>
  </div>
</template>
