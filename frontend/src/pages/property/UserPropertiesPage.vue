<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import useUserProperties from '@/queries/useUserProperties'
import type { PropertyPaginationRequest } from '@/services/property'
import PropertiesList from '@/components/PropertiesList.vue'

const route = useRoute()
const router = useRouter()

const paginationData = computed<PropertyPaginationRequest>(() => {
  const q = route.query
  const toNumber = (val: unknown) => (val ? Number(val) : undefined)

  return {
    page: toNumber(q.page) || 0,
    size: toNumber(q.size) || 20,
    sortField: q.sortField as string | undefined,
    sortDirection: q.sortDirection as 'ASC' | 'DESC' | undefined,
  }
})

const { data, isPending, isError } = useUserProperties(paginationData)

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
</script>

<template>
  <div class="grow flex flex-col items-center text-center px-4">
    <div class="w-full max-w-6xl py-8 text-left">
      <!-- Page Header -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 mb-2">Moje ogłoszenia</h1>
      </div>

      <!-- Properties List Component -->
      <PropertiesList
        :data="data"
        :is-pending="isPending"
        :is-error="isError"
        :current-sort="currentSort"
        :show-badges="true"
        @change-page="changePage"
        @sort-change="handleSortChange"
      />
    </div>
  </div>
</template>
