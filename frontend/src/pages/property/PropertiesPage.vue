<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter, type LocationQueryRaw } from 'vue-router'
import usePropertyPage from '@/queries/usePropertyPage'
import type { PropertyPaginationRequest, SearchFilters } from '@/services/property'
import PropertySearchForm from '@/components/property/PropertySearchForm.vue'
import PropertiesList from '@/components/property/PropertiesList.vue'

const route = useRoute()
const router = useRouter()

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

const { data, isPending, isError } = usePropertyPage(paginationData)

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
  <div class="grow flex flex-col items-center text-center px-4">
    <div class="w-full max-w-6xl py-8 text-left">
      <!-- Search Form -->
      <div class="mb-8">
        <PropertySearchForm :initial-filters="searchFilters" @search="handleSearch" />
      </div>

      <!-- Properties List Component -->
      <PropertiesList
        :data="data"
        :is-pending="isPending"
        :is-error="isError"
        :current-sort="currentSort"
        @change-page="changePage"
        @sort-change="handleSortChange"
      />
    </div>
  </div>
</template>
