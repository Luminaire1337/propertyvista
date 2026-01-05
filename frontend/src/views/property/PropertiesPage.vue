<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, type LocationQueryRaw } from 'vue-router'
import { MapPin, Maximize2, DoorOpen, Car } from 'lucide-vue-next'
import usePropertyPage from '@/queries/usePropertyPage'
import type { PropertyPaginationRequest, SearchFilters } from '@/services/property'
import PropertySearchForm from '@/components/PropertySearchForm.vue'
import { formatPrice, getRoomsLabel, getPropertiesLabel } from '@/utils'
import router from '@/router'

const route = useRoute()

const paginationData = computed<PropertyPaginationRequest>(() => {
  const q = route.query
  const toNumber = (val: unknown) => (val ? Number(val) : undefined)
  const toBool = (val: unknown) => (val === 'true' ? true : val === 'false' ? false : undefined)

  return {
    page: toNumber(q.page) || 0,
    size: toNumber(q.size) || 20,
    sortField: q.sortField as string | undefined,
    sortDirection: q.sortDirection as 'ASC' | 'DESC' | undefined,
    city: q.city as string | undefined,
    minPrice: toNumber(q.minPrice),
    maxPrice: toNumber(q.maxPrice),
    minRooms: toNumber(q.minRooms),
    maxRooms: toNumber(q.maxRooms),
    minArea: toNumber(q.minArea),
    maxArea: toNumber(q.maxArea),
    parking: toBool(q.parking),
  }
})

const { data, isPending, isError } = usePropertyPage(paginationData.value)

const handleSearch = (filters: SearchFilters) => {
  router.replace({ path: route.path, query: filters as LocationQueryRaw })
}

const changePage = (page: number) => {
  router.replace({
    path: route.path,
    query: {
      ...route.query,
      page,
    },
  })
}

const currentSort = computed(() => {
  const { sortField, sortDirection } = paginationData.value
  if (sortField === 'price' && sortDirection === 'ASC') return 'cheapest'
  if (sortField === 'price' && sortDirection === 'DESC') return 'expensive'
  if (sortField === 'createdAt' && sortDirection === 'ASC') return 'oldest'
  return 'newest'
})

const handleSortChange = (event: Event) => {
  const value = (event.target as HTMLSelectElement).value
  let sortField = 'createdAt'
  let sortDirection = 'DESC'

  switch (value) {
    case 'cheapest':
      sortField = 'price'
      sortDirection = 'ASC'
      break
    case 'expensive':
      sortField = 'price'
      sortDirection = 'DESC'
      break
    case 'oldest':
      sortField = 'createdAt'
      sortDirection = 'ASC'
      break
    case 'newest':
    default:
      sortField = 'createdAt'
      sortDirection = 'DESC'
      break
  }

  router.replace({
    path: route.path,
    query: {
      ...route.query,
      sortField,
      sortDirection,
    },
  })
}

const searchFilters = computed<SearchFilters>(() => {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { page, size, sortField, sortDirection, ...filters } = paginationData.value
  return filters as SearchFilters
})
</script>

<template>
  <div class="grow flex flex-col items-center justify-center text-center px-4">
    <div class="w-full max-w-6xl py-8 text-left">
      <!-- Search Form -->
      <div class="mb-8">
        <PropertySearchForm :initial-filters="searchFilters" @search="handleSearch" />
      </div>

      <!-- Error State -->
      <div v-if="isError" class="text-center py-12">
        <h2 class="text-lg font-medium text-gray-900 mb-2">Wystąpił błąd</h2>
        <p class="text-gray-600">Nie udało się pobrać nieruchomości</p>
      </div>

      <!-- Loading Skeleton -->
      <div v-else-if="isPending" class="space-y-4">
        <div
          v-for="i in 8"
          :key="i"
          class="bg-white rounded-lg shadow-md overflow-hidden flex flex-col sm:flex-row h-auto sm:h-52"
        >
          <div class="w-full sm:w-80 h-48 sm:h-full bg-gray-300 animate-pulse shrink-0"></div>
          <div class="flex-1 p-5 space-y-3">
            <div class="h-6 bg-gray-300 rounded animate-pulse w-3/4"></div>
            <div class="h-4 bg-gray-300 rounded animate-pulse w-1/2"></div>
            <div class="flex gap-4 mt-4">
              <div class="h-5 bg-gray-300 rounded animate-pulse w-24"></div>
              <div class="h-5 bg-gray-300 rounded animate-pulse w-20"></div>
              <div class="h-5 bg-gray-300 rounded animate-pulse w-20"></div>
            </div>
            <div class="h-8 bg-gray-300 rounded animate-pulse w-40 mt-auto"></div>
          </div>
        </div>
      </div>

      <!-- Properties Grid -->
      <div v-else-if="data">
        <!-- Results Count & Sort -->
        <div
          class="mb-4 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4"
        >
          <p class="text-gray-600">
            Znaleziono <span class="font-semibold">{{ data.totalElements }}</span>
            {{ getPropertiesLabel(data.totalElements) }}
          </p>

          <div class="flex items-center gap-2">
            <label for="sort" class="text-sm text-gray-600">Sortuj:</label>
            <select
              id="sort"
              :value="currentSort"
              @change="handleSortChange"
              class="focus:border-primary-500 focus:ring-primary-500 sm:text-sm py-1.5"
            >
              <option value="newest">Najnowsze</option>
              <option value="oldest">Najstarsze</option>
              <option value="cheapest">Najtańsze</option>
              <option value="expensive">Najdroższe</option>
            </select>
          </div>
        </div>

        <!-- No Results -->
        <div v-if="data.content.length === 0" class="text-center py-12">
          <h2 class="text-lg font-medium text-gray-900 mb-2">Brak wyników</h2>
          <p class="text-gray-600">Nie znaleziono nieruchomości spełniających wybrane kryteria</p>
        </div>

        <!-- Properties List -->
        <div v-else class="space-y-4">
          <RouterLink
            v-for="property in data.content"
            :key="property.id"
            :to="`/properties/${property.slug}`"
            class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-200 flex flex-col sm:flex-row"
          >
            <!-- Property Image -->
            <div class="relative w-full sm:w-80 h-48 sm:h-52 bg-gray-200 shrink-0">
              <img
                v-if="property.primaryImagePath"
                :src="property.primaryImagePath"
                :alt="property.title"
                loading="lazy"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center text-gray-400">
                <span class="text-sm">Brak zdjęcia</span>
              </div>
            </div>

            <!-- Property Details -->
            <div class="flex-1 p-5 flex flex-col">
              <!-- Header with Title -->
              <div class="mb-3">
                <h3 class="text-xl font-semibold text-gray-900 mb-2 line-clamp-2">
                  {{ property.title }}
                </h3>

                <!-- Location -->
                <div class="flex items-center gap-2 text-gray-600">
                  <MapPin class="w-4 h-4 shrink-0" />
                  <span class="text-sm">{{ property.city }}</span>
                </div>
              </div>

              <!-- Property Features and Price -->
              <div
                class="flex flex-col sm:flex-row justify-between items-start sm:items-end flex-1 gap-4 sm:gap-0"
              >
                <!-- Features -->
                <div class="flex flex-wrap items-center gap-4 sm:gap-6 text-gray-700">
                  <div v-if="property.rooms" class="flex items-center gap-2">
                    <DoorOpen class="w-5 h-5 text-gray-500" />
                    <span class="text-sm font-medium"
                      >{{ property.rooms }} {{ getRoomsLabel(property.rooms) }}</span
                    >
                  </div>
                  <div v-if="property.area" class="flex items-center gap-2">
                    <Maximize2 class="w-5 h-5 text-gray-500" />
                    <span class="text-sm font-medium">{{ property.area }} m²</span>
                  </div>
                  <div v-if="property.parking" class="flex items-center gap-2">
                    <Car class="w-5 h-5 text-primary" />
                    <span class="text-sm font-medium text-primary">Miejsce postojowe</span>
                  </div>
                </div>

                <!-- Price Section -->
                <div class="text-left sm:text-right w-full sm:w-auto">
                  <div class="text-2xl sm:text-3xl font-bold text-primary mb-1">
                    {{ formatPrice(property.price) }}
                  </div>
                  <div v-if="property.area" class="text-sm text-gray-500">
                    {{ formatPrice(property.price / property.area) }}/m²
                  </div>
                </div>
              </div>
            </div>
          </RouterLink>
        </div>

        <!-- Pagination -->
        <div v-if="data.totalPages > 1" class="mt-8 flex justify-center items-center gap-2">
          <button
            :disabled="data.pageNumber === 0"
            class="px-4 py-2 rounded-md border border-gray-300 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            @click="changePage(data.pageNumber - 1)"
          >
            Poprzednia
          </button>

          <div class="flex gap-1 overflow-x-auto max-w-sm">
            <button
              v-for="page in Math.min(data.totalPages, 10)"
              :key="page"
              :class="[
                'px-4 py-2 rounded-md border shrink-0',
                data.pageNumber === page - 1
                  ? 'bg-primary text-white border-primary'
                  : 'border-gray-300 bg-white hover:bg-gray-50',
              ]"
              @click="changePage(page - 1)"
            >
              {{ page }}
            </button>
            <span v-if="data.totalPages > 10" class="px-2 py-2 text-gray-500">...</span>
          </div>

          <button
            :disabled="data.pageNumber >= data.totalPages - 1"
            class="px-4 py-2 rounded-md border border-gray-300 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            @click="changePage(data.pageNumber + 1)"
          >
            Następna
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
