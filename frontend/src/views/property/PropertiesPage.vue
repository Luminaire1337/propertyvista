<script setup lang="ts">
import { computed, watch } from 'vue'
import { useRoute, type LocationQueryRaw } from 'vue-router'
import { MapPin, Maximize2, DoorOpen, Car } from 'lucide-vue-next'
import usePropertyPage from '@/queries/usePropertyPage'
import type { PropertyPaginationRequest, SearchFilters } from '@/services/property'
import PropertySearchForm from '@/components/PropertySearchForm.vue'
import { formatPrice } from '@/utils'
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

const { data, isPending, isError, error, refetch } = usePropertyPage(paginationData.value)

watch(paginationData, () => {
  refetch()
})

const handleSearch = (filters: SearchFilters) => {
  router.push({ query: filters as LocationQueryRaw })
}

const changePage = (page: number) => {
  router.push({
    query: {
      ...route.query,
      page,
    },
  })
}

const getRoomsLabel = (count: number) => {
  if (count === 1) return 'pokój'
  if (count >= 2 && count <= 4) return 'pokoje'
  return 'pokoi'
}

const getPropertiesLabel = (count: number) => {
  return count === 1 ? 'nieruchomość' : 'nieruchomości'
}

const searchFilters = computed<SearchFilters>(() => {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { page, size, sortField, sortDirection, ...filters } = paginationData.value
  return filters as SearchFilters
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="container mx-auto px-4 py-8 max-w-6xl">
      <!-- Search Form -->
      <div class="mb-8">
        <PropertySearchForm :initial-filters="searchFilters" @search="handleSearch" />
      </div>

      <!-- Error State -->
      <div v-if="isError" class="text-center py-12">
        <div class="bg-white rounded shadow-md p-6 max-w-md mx-auto">
          <h2 class="text-xl font-semibold text-red-800 mb-2">Wystąpił błąd</h2>
          <p class="text-red-600">{{ error?.message || 'Nie udało się pobrać nieruchomości' }}</p>
        </div>
      </div>

      <!-- Loading Skeleton -->
      <div v-else-if="isPending" class="space-y-4">
        <div
          v-for="i in 8"
          :key="i"
          class="bg-white rounded-lg shadow-md overflow-hidden flex h-52"
        >
          <div class="w-80 h-full bg-gray-300 animate-pulse shrink-0"></div>
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
        <!-- Results Count -->
        <div class="mb-4 text-gray-600">
          <p>
            Znaleziono <span class="font-semibold">{{ data.totalElements }}</span>
            {{ getPropertiesLabel(data.totalElements) }}
          </p>
        </div>

        <!-- No Results -->
        <div v-if="data.content.length === 0" class="text-center py-12">
          <div class="bg-white rounded shadow-md p-8 max-w-md mx-auto">
            <h2 class="text-2xl font-semibold text-gray-800 mb-2">Brak wyników</h2>
            <p class="text-gray-600">Nie znaleziono nieruchomości spełniających wybrane kryteria</p>
          </div>
        </div>

        <!-- Properties List -->
        <div v-else class="space-y-4">
          <RouterLink
            v-for="property in data.content"
            :key="property.id"
            :to="`/property/${property.slug}`"
            class="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-200 flex"
          >
            <!-- Property Image -->
            <div class="relative w-80 h-52 bg-gray-200 shrink-0">
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
              <div class="flex justify-between items-end flex-1">
                <!-- Features -->
                <div class="flex items-center gap-6 text-gray-700">
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
                <div class="text-right">
                  <div class="text-3xl font-bold text-primary mb-1">
                    {{ formatPrice(property.price) }}
                  </div>
                  <div v-if="property.area" class="text-sm text-gray-500">
                    {{ Math.round(property.price / property.area).toLocaleString('pl-PL') }} zł/m²
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
