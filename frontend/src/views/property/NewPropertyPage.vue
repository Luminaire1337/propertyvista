<script setup lang="ts">
import { ref, computed } from 'vue'
import { useCreatePropertyMutation } from '@/mutations/property'
import PrimaryButton from '@/components/PrimaryButton.vue'
import { X, ImagePlus, Check, AlertCircle } from 'lucide-vue-next'
import type { CreatePropertyRequest } from '@/services/property'
import useCurrentUser from '@/queries/useCurrentUser'

const createPropertyMutation = useCreatePropertyMutation()
const { data: user } = useCurrentUser()

// Form data
const formData = ref<Omit<CreatePropertyRequest, 'images' | 'primaryImagePath'>>({
  title: '',
  description: '',
  price: 0,
  city: '',
  area: 0,
  rooms: 0,
  parking: false,
  daysValid: 0,
})

// Images handling
const selectedImages = ref<File[]>([])
const imagePreviews = ref<{ file: File; url: string }[]>([])
const primaryImageIndex = ref<number>(0)
const fileInput = ref<HTMLInputElement | null>(null)

const MAX_IMAGES = 15

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = Array.from(target.files || [])

  const availableSlots = MAX_IMAGES - selectedImages.value.length
  const filesToAdd = files.slice(0, availableSlots)

  filesToAdd.forEach((file) => {
    selectedImages.value.push(file)
    imagePreviews.value.push({
      file,
      url: URL.createObjectURL(file),
    })
  })

  // Clear input
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const removeImage = (index: number) => {
  const preview = imagePreviews.value[index]
  if (preview) {
    URL.revokeObjectURL(preview.url)
  }
  selectedImages.value.splice(index, 1)
  imagePreviews.value.splice(index, 1)

  // Adjust primary image index
  if (primaryImageIndex.value === index) {
    primaryImageIndex.value = 0
  } else if (primaryImageIndex.value > index) {
    primaryImageIndex.value--
  }
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const setPrimaryImage = (index: number) => {
  primaryImageIndex.value = index
}

const canAddMore = computed(() => selectedImages.value.length < MAX_IMAGES)

// Property points calculation
const propertyPoints = computed(() => {
  if (formData.value.daysValid < 1) return 0
  return formData.value.daysValid
})

const hasEnoughPoints = computed(() => {
  return (user.value?.propertyPoints ?? 0) >= propertyPoints.value
})

const handleSubmit = () => {
  if (!formData.value.title.trim()) {
    return
  }

  if (selectedImages.value.length === 0) {
    return
  }

  createPropertyMutation.mutate({
    ...formData.value,
    images: selectedImages.value,
    primaryImagePath: imagePreviews.value[primaryImageIndex.value]?.file.name || '',
  })
}

const isFormValid = computed(() => {
  return (
    formData.value.title.trim() &&
    formData.value.price &&
    formData.value.city.trim() &&
    formData.value.area &&
    formData.value.rooms &&
    formData.value.daysValid &&
    selectedImages.value.length > 0 &&
    hasEnoughPoints.value
  )
})
</script>

<template>
  <div class="grow flex flex-col items-center px-4 py-8">
    <div class="w-full max-w-4xl">
      <h1 class="text-4xl font-bold text-gray-900 mb-2 text-center">Dodaj nowe ogłoszenie</h1>

      <form @submit.prevent="handleSubmit" class="space-y-8">
        <!-- Image Upload Section -->
        <div class="bg-white rounded shadow-md p-6">
          <h2 class="text-xl font-semibold mb-4">Zdjęcia nieruchomości</h2>

          <!-- Info Notice -->
          <div class="bg-blue-50 border border-blue-200 rounded p-4 mb-6">
            <p class="text-sm text-blue-800">
              <strong>Informacja:</strong> Po przesłaniu zdjęć, są one przekazywane do zewnętrznej
              usługi do przetwarzania obrazów. Weryfikacja może potrwać kilka minut.
            </p>
          </div>

          <!-- Image Previews Grid -->
          <div v-if="imagePreviews.length > 0" class="mb-6">
            <h3 class="text-sm font-medium text-gray-700 mb-3">
              Wybrane zdjęcia ({{ imagePreviews.length }}/{{ MAX_IMAGES }})
            </h3>
            <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4 mb-4">
              <div
                v-for="(preview, index) in imagePreviews"
                :key="index"
                class="relative aspect-square group"
              >
                <img
                  :src="preview.url"
                  :alt="`Zdjęcie ${index + 1}`"
                  class="w-full h-full object-cover rounded border-2"
                  :class="
                    primaryImageIndex === index
                      ? 'border-primary ring-2 ring-primary'
                      : 'border-gray-200'
                  "
                />
                <button
                  @click="setPrimaryImage(index)"
                  type="button"
                  class="absolute top-2 left-2 p-1.5 rounded-full transition-colors"
                  :class="
                    primaryImageIndex === index
                      ? 'bg-primary text-white'
                      : 'bg-white/90 text-gray-700 hover:bg-white opacity-0 group-hover:opacity-100'
                  "
                  :title="primaryImageIndex === index ? 'Główne zdjęcie' : 'Ustaw jako główne'"
                >
                  <Check class="w-4 h-4" />
                </button>
                <button
                  @click="removeImage(index)"
                  type="button"
                  class="absolute top-2 right-2 bg-red-500 text-white p-1.5 rounded-full hover:bg-red-600 transition-colors opacity-0 group-hover:opacity-100"
                  title="Usuń zdjęcie"
                >
                  <X class="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>

          <!-- Upload Button -->
          <div>
            <input
              ref="fileInput"
              type="file"
              accept="image/png, image/jpeg"
              multiple
              @change="handleFileSelect"
              class="hidden"
            />
            <button
              v-if="canAddMore"
              @click="triggerFileInput"
              type="button"
              class="w-full px-4 py-3 border-2 border-dashed border-gray-300 rounded hover:border-primary hover:bg-gray-50 transition-colors flex items-center justify-center gap-2 text-gray-700 hover:text-primary"
            >
              <ImagePlus class="w-5 h-5" />
              <span class="font-medium">
                {{ imagePreviews.length === 0 ? 'Dodaj zdjęcia' : 'Dodaj więcej zdjęć' }}
              </span>
            </button>
            <p v-else class="text-sm text-gray-600">Osiągnięto limit {{ MAX_IMAGES }} zdjęć</p>
            <p class="text-xs text-gray-500 mt-2">
              Obsługiwane formaty: PNG, JPG. Maksymalnie {{ MAX_IMAGES }} zdjęć.
            </p>
          </div>
        </div>

        <!-- Property Details Section -->
        <div class="bg-white rounded shadow-md p-6">
          <h2 class="text-xl font-semibold mb-4">Szczegóły nieruchomości</h2>

          <div class="space-y-4">
            <!-- Title -->
            <div>
              <label for="title" class="block text-sm font-medium text-gray-700 mb-2">
                Tytuł ogłoszenia <span class="text-red-500">*</span>
              </label>
              <input
                id="title"
                v-model="formData.title"
                type="text"
                required
                class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
                placeholder="np. Przestronne mieszkanie w centrum miasta"
              />
            </div>

            <!-- Description -->
            <div>
              <label for="description" class="block text-sm font-medium text-gray-700 mb-2">
                Opis
              </label>
              <textarea
                id="description"
                v-model="formData.description"
                rows="5"
                class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
                placeholder="Szczegółowy opis nieruchomości..."
              ></textarea>
            </div>

            <!-- Price & City -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label for="price" class="block text-sm font-medium text-gray-700 mb-2">
                  Cena (PLN) <span class="text-red-500">*</span>
                </label>
                <input
                  id="price"
                  v-model="formData.price"
                  type="number"
                  step="0.01"
                  min="0"
                  required
                  class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
                  placeholder="500000"
                />
              </div>

              <div>
                <label for="city" class="block text-sm font-medium text-gray-700 mb-2">
                  Miasto <span class="text-red-500">*</span>
                </label>
                <input
                  id="city"
                  v-model="formData.city"
                  type="text"
                  required
                  class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
                  placeholder="np. Warszawa"
                />
              </div>
            </div>

            <!-- Area & Rooms -->
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label for="area" class="block text-sm font-medium text-gray-700 mb-2">
                  Powierzchnia (m²) <span class="text-red-500">*</span>
                </label>
                <input
                  id="area"
                  v-model="formData.area"
                  type="number"
                  step="0.01"
                  min="0"
                  required
                  class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
                  placeholder="65"
                />
              </div>

              <div>
                <label for="rooms" class="block text-sm font-medium text-gray-700 mb-2">
                  Liczba pokoi <span class="text-red-500">*</span>
                </label>
                <input
                  id="rooms"
                  v-model="formData.rooms"
                  type="number"
                  min="1"
                  required
                  class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
                  placeholder="3"
                />
              </div>
            </div>

            <!-- Parking -->
            <div class="flex items-center">
              <input
                id="parking"
                v-model="formData.parking"
                type="checkbox"
                class="w-4 h-4 accent-primary border-gray-300 rounded focus:ring-primary"
              />
              <label for="parking" class="ml-2 text-sm text-gray-700">Miejsce postojowe</label>
            </div>
          </div>
        </div>

        <!-- Property Points -->
        <div class="bg-white rounded shadow-md p-6">
          <div class="flex items-center justify-between mb-6">
            <h2 class="text-xl font-semibold">Ważność ogłoszenia</h2>
            <div class="text-right">
              <p class="text-xs text-gray-500">Dostępne Property Points</p>
              <p class="text-lg font-bold text-primary">{{ user?.propertyPoints ?? 0 }}</p>
            </div>
          </div>

          <div class="mb-4">
            <label for="daysValid" class="block text-sm font-medium text-gray-700 mb-2">
              Ile dni ma być widoczna oferta? <span class="text-red-500">*</span>
            </label>
            <input
              id="daysValid"
              v-model="formData.daysValid"
              type="number"
              min="1"
              max="90"
              required
              class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
              placeholder="np. 30"
            />
            <p class="text-xs text-gray-500 mt-1">1 dzień = 1 Property Point</p>
          </div>

          <!-- Transaction Preview -->
          <div v-if="formData.daysValid > 0" class="border-t pt-4">
            <div class="flex items-center justify-between text-sm mb-2">
              <span class="text-gray-600">Koszt:</span>
              <span class="font-medium"
                >{{ propertyPoints }} {{ propertyPoints === 1 ? 'punkt' : 'punktów' }}</span
              >
            </div>
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-600">Pozostanie:</span>
              <span class="font-bold" :class="hasEnoughPoints ? 'text-gray-900' : 'text-red-600'">
                {{ (user?.propertyPoints ?? 0) - propertyPoints }}
                {{
                  Math.abs((user?.propertyPoints ?? 0) - propertyPoints) === 1 ? 'punkt' : 'punktów'
                }}
              </span>
            </div>

            <!-- Warning if not enough points -->
            <div v-if="!hasEnoughPoints" class="mt-4 bg-red-50 border border-red-200 rounded p-3">
              <p class="text-sm text-red-700 flex items-center gap-2">
                <AlertCircle class="w-4 h-4 shrink-0" />
                <span>
                  Brakuje {{ propertyPoints - (user?.propertyPoints ?? 0) }}
                  {{ propertyPoints - (user?.propertyPoints ?? 0) === 1 ? 'punktu' : 'punktów' }}
                </span>
              </p>
            </div>
          </div>
        </div>

        <!-- Submit Button -->
        <div class="flex justify-end">
          <PrimaryButton
            type="submit"
            :disabled="!isFormValid || createPropertyMutation.isPending.value"
            custom-class="w-full sm:w-auto px-8"
          >
            {{ createPropertyMutation.isPending.value ? 'Dodawanie...' : 'Dodaj nieruchomość' }}
          </PrimaryButton>
        </div>
      </form>
    </div>
  </div>
</template>
