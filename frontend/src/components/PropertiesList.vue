<script setup lang="ts">
import { ref } from 'vue'
import { MapPin, Maximize2, DoorOpen, Car, Clock } from 'lucide-vue-next'
import type { PropertyPage } from '@/services/property'
import { formatPrice, getRoomsLabel, getPropertiesLabel, formatDate } from '@/utils'
import PrimaryButton from '@/components/PrimaryButton.vue'
import ConfirmationModal from '@/components/ConfirmationModal.vue'
import { useDeletePropertyMutation } from '@/mutations/property'

defineProps<{
  data?: PropertyPage
  isPending: boolean
  isError: boolean
  currentSort?: string
  showBadges?: boolean
}>()

defineEmits<{
  changePage: [page: number]
  sortChange: [event: Event]
}>()

const deletePropertyMutation = useDeletePropertyMutation()
const isDeleteModalOpen = ref(false)
const propertyToDelete = ref<{ slug: string; title: string } | null>(null)

const openDeleteModal = (slug: string, title: string) => {
  propertyToDelete.value = { slug, title }
  isDeleteModalOpen.value = true
}

const closeDeleteModal = () => {
  isDeleteModalOpen.value = false
  propertyToDelete.value = null
}

const handleDelete = () => {
  if (propertyToDelete.value) {
    deletePropertyMutation.mutate(propertyToDelete.value.slug)
    closeDeleteModal()
  }
}

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    PUBLISHED: 'Ogłoszenie aktywne',
    UNVERIFIED: 'Ogłoszenie oczekuje na weryfikację',
    EXPIRED: 'Ogłoszenie wygasło',
    HIDDEN: 'Ogłoszenie zostało ukryte/zawieszone',
  }
  return labels[status] || status
}

const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    PUBLISHED: 'bg-green-100 text-green-800',
    UNVERIFIED: 'bg-yellow-100 text-yellow-800',
    EXPIRED: 'bg-red-100 text-red-800',
    HIDDEN: 'bg-gray-100 text-gray-800',
  }
  return colors[status] || 'bg-gray-100 text-gray-800'
}
</script>

<template>
  <!-- Error State -->
  <div v-if="isError" class="text-center py-12">
    <h2 class="text-lg font-medium text-gray-900 mb-2">Wystąpił błąd</h2>
    <p class="text-gray-600">Nie udało się pobrać listy ogłoszeń. Spróbuj ponownie później.</p>
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
    <div class="mb-4 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
      <p class="text-gray-600">
        Znaleziono <span class="font-semibold">{{ data.totalElements }}</span>
        {{ getPropertiesLabel(data.totalElements) }}
      </p>

      <div v-if="currentSort !== undefined" class="flex items-center gap-2">
        <label for="sort" class="text-sm text-gray-600">Sortuj:</label>
        <select
          id="sort"
          :value="currentSort"
          @change="$emit('sortChange', $event)"
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
      <p class="text-gray-600">Nie znaleziono ogłoszeń spełniających podane kryteria.</p>
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

          <!-- Status & Expiry Badges -->
          <div v-if="showBadges" class="absolute top-3 left-3 flex flex-col gap-2 items-start">
            <span
              v-if="property.status"
              :class="[
                'px-2.5 py-1 rounded-md text-xs font-semibold shadow-sm',
                getStatusColor(property.status),
              ]"
            >
              {{ getStatusLabel(property.status) }}
            </span>
            <span
              v-if="property.expiryDate"
              class="px-2.5 py-1 rounded-md text-xs font-semibold bg-white/90 text-gray-700 shadow-sm flex items-center gap-1"
            >
              <Clock class="w-3 h-3" />
              {{ formatDate(property.expiryDate) }}
            </span>
          </div>
        </div>

        <!-- Property Details -->
        <div class="flex-1 p-5 flex flex-col">
          <!-- Header with Title -->
          <div class="mb-3">
            <!-- Header with Title and Edit Button -->
            <div class="flex justify-between items-start gap-4 mb-2">
              <!-- Title -->
              <h3 class="text-xl font-semibold text-gray-900 line-clamp-2">
                {{ property.title }}
              </h3>

              <!-- Edit and Delete buttons aligned with title -->
              <div v-if="showBadges" class="flex gap-2 shrink-0">
                <RouterLink :to="`/properties/${property.slug}/edit`">
                  <PrimaryButton>Edytuj</PrimaryButton>
                </RouterLink>
                <PrimaryButton
                  @click.prevent="openDeleteModal(property.slug, property.title)"
                  custom-class="!bg-red-600 hover:!bg-red-700"
                >
                  Usuń
                </PrimaryButton>
              </div>
            </div>

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
        @click="$emit('changePage', data.pageNumber - 1)"
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
          @click="$emit('changePage', page - 1)"
        >
          {{ page }}
        </button>
        <span v-if="data.totalPages > 10" class="px-2 py-2 text-gray-500">...</span>
      </div>

      <button
        :disabled="data.pageNumber >= data.totalPages - 1"
        class="px-4 py-2 rounded-md border border-gray-300 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        @click="$emit('changePage', data.pageNumber + 1)"
      >
        Następna
      </button>
    </div>

    <!-- Delete Property Modal -->
    <ConfirmationModal
      :is-open="isDeleteModalOpen"
      title="Usuń ogłoszenie"
      :description="`Czy na pewno chcesz usunąć ogłoszenie: ${propertyToDelete?.title}? Ta operacja jest nieodwracalna.`"
      :confirm-text="deletePropertyMutation.isPending.value ? 'Usuwanie...' : 'Usuń ogłoszenie'"
      cancel-text="Anuluj"
      :is-pending="deletePropertyMutation.isPending.value"
      variant="danger"
      @close="closeDeleteModal"
      @confirm="handleDelete"
    />
  </div>
</template>
