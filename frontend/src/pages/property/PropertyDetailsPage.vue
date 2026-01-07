<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import usePropertyDetails from '@/queries/usePropertyDetails'
import { formatDate, formatPrice, getRoomsLabel } from '@/utils'
import {
  MapPin,
  Maximize2,
  DoorOpen,
  Car,
  Phone,
  Mail,
  ChevronLeft,
  ChevronRight,
  X,
  Clock,
} from 'lucide-vue-next'
import AvatarImage from '@/components/AvatarImage.vue'
import { Dialog, DialogPanel, TransitionRoot, TransitionChild } from '@headlessui/vue'

const route = useRoute()
const slug = computed(() => route.params.slug as string)

const { data, isPending, isError } = usePropertyDetails(slug.value)

const showContactInfo = ref(false)
const currentImageIndex = ref(0)
const isLightboxOpen = ref(false)

const imagePaths = computed(() => {
  // Set primary image first
  if (
    !data.value?.imagePaths ||
    !data.value?.primaryImagePath ||
    data.value.imagePaths.length === 0
  )
    return []
  const primaryImage = data.value.primaryImagePath
  return [primaryImage, ...data.value.imagePaths.filter((img) => img !== primaryImage)]
})

const nextImage = (e?: Event) => {
  e?.stopPropagation()
  if (!imagePaths.value.length) return
  currentImageIndex.value = (currentImageIndex.value + 1) % imagePaths.value.length
}

const prevImage = (e?: Event) => {
  e?.stopPropagation()
  if (!imagePaths.value.length) return
  currentImageIndex.value =
    (currentImageIndex.value - 1 + imagePaths.value.length) % imagePaths.value.length
}

const setImage = (index: number, e?: Event) => {
  e?.stopPropagation()
  currentImageIndex.value = index
}

const openLightbox = () => {
  if (imagePaths.value.length) {
    isLightboxOpen.value = true
  }
}

const closeLightbox = () => {
  isLightboxOpen.value = false
}
</script>

<template>
  <div class="grow flex flex-col items-center px-4">
    <div v-if="isPending" class="w-full max-w-7xl py-8 text-left">
      <!-- Image Skeleton -->
      <div class="w-full h-100 md:h-125 bg-gray-300 rounded-xl mb-8 animate-pulse"></div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div class="lg:col-span-2 space-y-8">
          <!-- Header Skeleton -->
          <div class="border-b border-gray-200 pb-6 space-y-4">
            <div class="h-10 bg-gray-300 rounded w-3/4 animate-pulse"></div>
            <div class="h-6 bg-gray-300 rounded w-1/4 animate-pulse"></div>
            <div class="h-10 bg-gray-300 rounded w-1/3 animate-pulse"></div>
          </div>

          <!-- Details Skeleton -->
          <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <div v-for="i in 3" :key="i" class="h-24 bg-gray-300 rounded-xl animate-pulse"></div>
          </div>

          <!-- Description Skeleton -->
          <div class="space-y-2">
            <div v-for="i in 6" :key="i" class="h-4 bg-gray-300 rounded w-full animate-pulse"></div>
          </div>
        </div>

        <!-- Sidebar Skeleton -->
        <div class="lg:col-span-1">
          <div class="h-64 bg-gray-300 rounded-xl animate-pulse"></div>
        </div>
      </div>
    </div>

    <div v-else-if="isError || !data" class="text-center py-12">
      <h2 class="text-2xl font-bold text-gray-900">Nie znaleziono ogłoszenia</h2>
      <p class="text-gray-600 mt-2">
        Ogłoszenie, którego szukasz, nie istnieje lub zostało usunięte.
      </p>
    </div>

    <div v-else class="w-full max-w-7xl py-8 text-left">
      <!-- Image Carousel -->
      <div
        class="relative h-100 md:h-125 bg-gray-100 rounded overflow-hidden mb-8 group cursor-pointer"
        @click="openLightbox"
      >
        <img
          v-if="imagePaths && imagePaths.length > 0"
          :src="imagePaths[currentImageIndex]"
          class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
          :alt="`Zdjęcie-${currentImageIndex + 1}`"
          loading="lazy"
        />
        <div
          v-else
          class="w-full h-full flex items-center justify-center text-gray-400 bg-gray-200"
        >
          <span class="flex flex-col items-center">
            <Maximize2 class="w-12 h-12 mb-2 opacity-50" />
            Brak zdjęć
          </span>
        </div>

        <!-- Navigation Arrows -->
        <template v-if="imagePaths && imagePaths.length > 1">
          <button
            @click="prevImage"
            class="absolute left-4 top-1/2 -translate-y-1/2 bg-white/90 p-2 rounded-full hover:bg-white transition-colors shadow-lg opacity-0 group-hover:opacity-100 focus:opacity-100 z-10"
          >
            <ChevronLeft class="w-6 h-6 text-gray-800" />
          </button>
          <button
            @click="nextImage"
            class="absolute right-4 top-1/2 -translate-y-1/2 bg-white/90 p-2 rounded-full hover:bg-white transition-colors shadow-lg opacity-0 group-hover:opacity-100 focus:opacity-100 z-10"
          >
            <ChevronRight class="w-6 h-6 text-gray-800" />
          </button>

          <!-- Thumbnails/Dots -->
          <div class="absolute bottom-4 left-1/2 -translate-x-1/2 flex gap-2 z-10" @click.stop>
            <button
              v-for="(_, index) in imagePaths"
              :key="index"
              @click="setImage(index, $event)"
              class="w-2.5 h-2.5 rounded-full transition-all"
              :class="
                index === currentImageIndex ? 'bg-white scale-110' : 'bg-white/50 hover:bg-white/80'
              "
            />
          </div>
        </template>

        <!-- Hover overlay with maximize icon -->
        <div
          class="absolute inset-0 bg-black/0 group-hover:bg-black/10 transition-colors flex items-center justify-center opacity-0 group-hover:opacity-100 pointer-events-none"
        >
          <Maximize2 class="w-12 h-12 text-white drop-shadow-lg" />
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Main Content -->
        <div class="lg:col-span-2 space-y-8">
          <!-- Header Info -->
          <div class="border-b border-gray-200 pb-6">
            <h1 class="text-3xl font-bold text-gray-900 mb-3">{{ data.title }}</h1>
            <div class="flex items-center text-gray-600 mb-4">
              <MapPin class="w-5 h-5 mr-2 text-gray-400" />
              <span class="text-lg">{{ data.city }}</span>
            </div>
            <div>
              <div class="text-4xl font-bold text-primary">
                {{ formatPrice(data.price) }}
              </div>
              <div v-if="data.area" class="text-lg text-gray-500 mt-1">
                {{ formatPrice(data.price / data.area) }}/m²
              </div>
            </div>
          </div>

          <!-- Key Features -->
          <div>
            <h2 class="text-xl font-bold text-gray-900 mb-4">Szczegóły</h2>
            <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
              <div class="flex flex-col p-4 bg-gray-50 rounded border border-gray-100">
                <Maximize2 class="w-6 h-6 text-gray-400 mb-2" />
                <span class="text-sm text-gray-500">Powierzchnia</span>
                <span class="font-semibold text-lg">{{ data.area }} m²</span>
              </div>
              <div class="flex flex-col p-4 bg-gray-50 rounded border border-gray-100">
                <DoorOpen class="w-6 h-6 text-gray-400 mb-2" />
                <span class="text-sm text-gray-500 capitalize">{{
                  getRoomsLabel(data.rooms)
                }}</span>
                <span class="font-semibold text-lg">{{ data.rooms }}</span>
              </div>
              <div class="flex flex-col p-4 bg-gray-50 rounded border border-gray-100">
                <Car class="w-6 h-6 text-gray-400 mb-2" />
                <span class="text-sm text-gray-500">Miejsce postojowe</span>
                <span class="font-semibold text-lg">{{ data.parking ? 'Dostępne' : 'Brak' }}</span>
              </div>
              <div class="flex flex-col p-4 bg-gray-50 rounded border border-gray-100">
                <Clock class="w-6 h-6 text-gray-400 mb-2" />
                <span class="text-sm text-gray-500">Data dodania</span>
                <span class="font-semibold text-lg">{{ formatDate(data.createdAt) }}</span>
              </div>
            </div>
          </div>

          <!-- Description -->
          <div>
            <h2 class="text-xl font-bold text-gray-900 mb-4">Opis</h2>
            <div class="prose max-w-none text-gray-600 whitespace-pre-line leading-relaxed">
              {{ data.description }}
            </div>
          </div>
        </div>

        <!-- Sidebar -->
        <div class="lg:col-span-1">
          <div class="bg-white border border-gray-200 rounded p-6 top-24 shadow-sm">
            <div class="flex items-center space-x-4 mb-6 pb-6 border-b border-gray-100">
              <AvatarImage
                :src="data.user?.avatarImagePath"
                :size="64"
                :alt="`${data.user?.firstName} ${data.user?.lastName}`"
              />
              <div>
                <h3 class="font-bold text-lg text-gray-900">
                  {{ data.user?.firstName }} {{ data.user?.lastName }}
                </h3>
                <p class="text-sm text-gray-500">Osoba prywatna</p>
              </div>
            </div>

            <div class="space-y-4">
              <button
                v-if="!showContactInfo"
                @click="showContactInfo = true"
                class="w-full bg-primary text-white py-3 px-4 rounded font-semibold hover:bg-primary-dark transition-colors flex items-center justify-center shadow-sm hover:shadow-md cursor-pointer"
              >
                Skontaktuj się ze sprzedającym
              </button>

              <div v-else class="space-y-3">
                <div class="flex items-center p-3 bg-gray-50 rounded border border-gray-100">
                  <Phone class="w-5 h-5 text-primary mr-3" />
                  <div class="flex flex-col">
                    <span class="text-xs text-gray-500">Numer telefonu</span>
                    <a
                      v-if="data.user?.phoneNumber"
                      :href="`tel:${data.user.phoneNumber}`"
                      class="font-medium text-gray-900 hover:text-primary transition-colors"
                    >
                      {{ data.user.phoneNumber }}
                    </a>
                    <span v-else class="font-medium text-gray-900">Nie podano</span>
                  </div>
                </div>
                <div class="flex items-center p-3 bg-gray-50 rounded border border-gray-100">
                  <Mail class="w-5 h-5 text-primary mr-3" />
                  <div class="flex flex-col">
                    <span class="text-xs text-gray-500">Adres email</span>
                    <a
                      :href="`mailto:${data.user?.email}`"
                      class="font-medium text-gray-900 break-all hover:text-primary transition-colors"
                    >
                      {{ data.user?.email }}
                    </a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Lightbox Modal -->
    <TransitionRoot :show="isLightboxOpen" as="template">
      <Dialog @close="closeLightbox" class="relative z-50">
        <TransitionChild
          as="template"
          enter="ease-out duration-300"
          enter-from="opacity-0"
          enter-to="opacity-100"
          leave="ease-in duration-200"
          leave-from="opacity-100"
          leave-to="opacity-0"
        >
          <div class="fixed inset-0 bg-black/90" />
        </TransitionChild>

        <div class="fixed inset-0 flex items-center justify-center p-4">
          <TransitionChild
            as="template"
            enter="ease-out duration-300"
            enter-from="opacity-0 scale-95"
            enter-to="opacity-100 scale-100"
            leave="ease-in duration-200"
            leave-from="opacity-100 scale-100"
            leave-to="opacity-0 scale-95"
          >
            <DialogPanel class="w-full max-w-7xl rounded p-6">
              <div class="relative flex items-center justify-center h-[80vh]">
                <img
                  v-if="imagePaths && imagePaths.length > 0"
                  :src="imagePaths[currentImageIndex]"
                  class="max-h-full max-w-full object-contain"
                  :alt="`Zdjęcie-${currentImageIndex + 1}`"
                  loading="lazy"
                />

                <button
                  @click="closeLightbox"
                  class="absolute top-0 right-0 p-2 text-white hover:text-gray-300 transition-colors"
                >
                  <X class="w-8 h-8" />
                </button>

                <button
                  v-if="imagePaths && imagePaths.length > 1"
                  @click="prevImage"
                  class="absolute left-0 top-1/2 -translate-y-1/2 p-4 text-white hover:text-gray-300 transition-colors"
                >
                  <ChevronLeft class="w-10 h-10" />
                </button>

                <button
                  v-if="imagePaths && imagePaths.length > 1"
                  @click="nextImage"
                  class="absolute right-0 top-1/2 -translate-y-1/2 p-4 text-white hover:text-gray-300 transition-colors"
                >
                  <ChevronRight class="w-10 h-10" />
                </button>
              </div>

              <!-- Thumbnails in Lightbox -->
              <div class="mt-4 flex justify-center gap-2 overflow-x-auto py-2">
                <button
                  v-for="(_, index) in imagePaths"
                  :key="index"
                  @click="setImage(index)"
                  class="w-16 h-16 shrink-0 rounded overflow-hidden border-2 transition-all"
                  :class="
                    index === currentImageIndex
                      ? 'border-white opacity-100'
                      : 'border-transparent opacity-50 hover:opacity-80'
                  "
                >
                  <img :src="imagePaths[index]" class="w-full h-full object-cover" loading="lazy" />
                </button>
              </div>
            </DialogPanel>
          </TransitionChild>
        </div>
      </Dialog>
    </TransitionRoot>
  </div>
</template>
