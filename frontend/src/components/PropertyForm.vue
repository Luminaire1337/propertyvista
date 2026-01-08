<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import PrimaryButton from '@/components/PrimaryButton.vue'
import { X, ImagePlus, Check, AlertCircle, Clock } from 'lucide-vue-next'
import type {
  CreatePropertyRequest,
  PropertyDetails,
  UpdatePropertyRequest,
} from '@/services/property'
import useCurrentUser from '@/queries/useCurrentUser'
import { checkDifferenceBetweenArrays, formatDate, urlToFile } from '@/utils'
import type {
  useCreatePropertyMutation,
  usePartiallyUpdatePropertyMutation,
} from '@/mutations/property'
import { toast } from 'vue-sonner'

const props = defineProps<{
  initialData?: PropertyDetails
  submitText: string
  mutation:
    | ReturnType<typeof useCreatePropertyMutation>
    | ReturnType<typeof usePartiallyUpdatePropertyMutation>
  mode: 'create' | 'edit'
}>()

const emit = defineEmits<{
  submit: [data: CreatePropertyRequest | UpdatePropertyRequest]
}>()

const { data: user } = useCurrentUser()

// Form data
const formData = ref({
  // Only v-model bound fields
  title: props.initialData?.title || '',
  description: props.initialData?.description || '',
  price: props.initialData?.price || 0,
  city: props.initialData?.city || '',
  area: props.initialData?.area || 0,
  rooms: props.initialData?.rooms || 0,
  parking: props.initialData?.parking || false,
  daysValid: 0,
})

// Images handling
const initialImages = computed(() => props.initialData?.imagePaths || [])
const initialPrimaryImage = computed(() => props.initialData?.primaryImagePath || '')
const selectedImages = ref<File[]>([])
const imagePreviews = ref<{ file: File; url: string }[]>([])
const primaryImageIndex = ref<number>(0)
const fileInput = ref<HTMLInputElement | null>(null)

const MAX_IMAGES = 15

// Load selectedImages from initialImages in edit mode
onMounted(async () => {
  if (props.mode !== 'edit' || initialImages.value.length === 0) return

  selectedImages.value = await Promise.all(
    initialImages.value.map(async (url) => {
      const file = await urlToFile(url)
      imagePreviews.value.push({
        file,
        url: URL.createObjectURL(file),
      })
      if (url === initialPrimaryImage.value) {
        setPrimaryImage(imagePreviews.value.length - 1)
      }
      return file
    }),
  )
})

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

  // Adjust primary image index if needed
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
  if (!isFormValid.value) return

  const imageNames = imagePreviews.value.map((p) => p.file.name)
  const primaryImagePath = imageNames[primaryImageIndex.value] || ''

  // If it's create mode, just simply emit create data
  if (props.mode === 'create') {
    return emit('submit', {
      ...formData.value,
      images: selectedImages.value,
      primaryImagePath,
    } as CreatePropertyRequest)
  }

  // If it's not, calculated the diffs
  const updateData: UpdatePropertyRequest = {}

  if (formData.value.title !== props.initialData?.title) {
    updateData.title = formData.value.title
  }

  if (formData.value.description !== props.initialData?.description) {
    updateData.description = formData.value.description
  }

  if (formData.value.price !== props.initialData?.price) {
    updateData.price = formData.value.price
  }

  if (formData.value.city !== props.initialData?.city) {
    updateData.city = formData.value.city
  }

  if (formData.value.area !== props.initialData?.area) {
    updateData.area = formData.value.area
  }

  if (formData.value.rooms !== props.initialData?.rooms) {
    updateData.rooms = formData.value.rooms
  }

  if (formData.value.parking !== props.initialData?.parking) {
    updateData.parking = formData.value.parking
  }

  if (formData.value.daysValid > 0) {
    updateData.daysValid = formData.value.daysValid
  }

  // Image changes
  const initialImageFileNames = initialImages.value.map((url) => {
    const parts = url.split('/')
    return parts[parts.length - 1] || ''
  })
  const initialPrimaryImageFileName = (() => {
    const parts = initialPrimaryImage.value.split('/')
    return parts[parts.length - 1] || ''
  })()

  if (checkDifferenceBetweenArrays<string>(initialImageFileNames, imageNames)) {
    // selectedImages.value returns a proxy array, so we need to create a new array
    updateData.images = [...selectedImages.value]
    updateData.primaryImagePath = primaryImagePath
  } else if (primaryImagePath !== initialPrimaryImageFileName) {
    updateData.primaryImagePath = primaryImagePath
  }

  if (Object.keys(updateData).length === 0) {
    toast.warning('Brak zmian do zapisania.')
    return
  }

  emit('submit', updateData)
}

const isFormValid = computed(() => {
  const baseValid =
    formData.value.title.trim() &&
    formData.value.price &&
    formData.value.city.trim() &&
    formData.value.area &&
    formData.value.rooms &&
    selectedImages.value.length > 0

  // In edit mode, daysValid is optional (only if user wants to extend)
  if (props.mode === 'edit') {
    // If user entered days, check if they have enough points
    if (formData.value.daysValid > 0) {
      return baseValid && hasEnoughPoints.value
    }
    return baseValid
  }

  // In create mode, daysValid is required
  return baseValid && formData.value.daysValid && hasEnoughPoints.value
})

// Watch for initial data changes (useful for edit mode)
watch(
  () => props.initialData,
  (newData) => {
    if (newData) {
      formData.value = {
        title: newData.title || formData.value.title,
        description: newData.description || formData.value.description,
        price: newData.price || formData.value.price,
        city: newData.city || formData.value.city,
        area: newData.area || formData.value.area,
        rooms: newData.rooms || formData.value.rooms,
        parking: newData.parking || formData.value.parking,
        daysValid: formData.value.daysValid,
      }
    }
  },
  { deep: true },
)
</script>

<template>
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
      <div v-if="selectedImages.length > 0" class="mb-6">
        <h3 class="text-sm font-medium text-gray-700 mb-3">
          Wybrane zdjęcia ({{ selectedImages.length }}/{{ MAX_IMAGES }})
        </h3>
        <div class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4 mb-4">
          <!-- New Images -->
          <div
            v-for="(preview, index) in imagePreviews"
            :key="`Zdjęcie-${index}`"
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
            {{ selectedImages.length === 0 ? 'Dodaj zdjęcia' : 'Dodaj więcej zdjęć' }}
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
        <h2 class="text-xl font-semibold">
          {{ mode === 'create' ? 'Ważność ogłoszenia' : 'Przedłuż ważność ogłoszenia' }}
        </h2>
        <div class="text-right">
          <p class="text-xs text-gray-500">Dostępne Property Points</p>
          <p class="text-lg font-bold text-primary">{{ user?.propertyPoints ?? 0 }}</p>
        </div>
      </div>

      <div class="mb-4">
        <label for="daysValid" class="block text-sm font-medium text-gray-700 mb-2">
          {{ mode === 'create' ? 'Ile dni ma być widoczna oferta?' : 'O ile dni przedłużyć?' }}
          <span v-if="mode === 'create'" class="text-red-500">*</span>
        </label>
        <input
          id="daysValid"
          v-model="formData.daysValid"
          type="number"
          min="0"
          max="90"
          :required="mode === 'create'"
          class="w-full px-4 py-2 border rounded focus:outline-none focus:ring focus:border-green-300"
          :placeholder="mode === 'create' ? 'np. 30' : 'np. 7 (opcjonalne)'"
        />
        <p class="text-xs text-gray-500 mt-1">1 dzień = 1 Property Point</p>
      </div>

      <!-- Current Expiry Date (edit mode only) -->
      <div
        v-if="mode === 'edit' && props.initialData?.expiryDate"
        class="mb-4 bg-blue-50 border border-blue-200 rounded p-4"
      >
        <div class="flex items-center gap-2 text-sm text-blue-800 mb-2">
          <Clock class="w-4 h-4" />
          <span class="font-medium">Aktualna data wygaśnięcia:</span>
        </div>
        <p class="text-sm text-blue-900 font-semibold">
          {{ formatDate(props.initialData.expiryDate) }}
        </p>
        <p v-if="formData.daysValid > 0" class="text-sm text-blue-700 mt-2">
          <span class="font-medium">Nowa data wygaśnięcia:</span>
          {{
            formatDate(
              new Date(
                new Date(props.initialData.expiryDate).getTime() +
                  formData.daysValid * 24 * 60 * 60 * 1000,
              ).toISOString(),
            )
          }}
        </p>
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
            {{ Math.abs((user?.propertyPoints ?? 0) - propertyPoints) === 1 ? 'punkt' : 'punktów' }}
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
        :disabled="!isFormValid || mutation.isPending.value"
        custom-class="w-full sm:w-auto px-8"
      >
        {{ mutation.isPending.value ? 'Zapisywanie...' : submitText }}
      </PrimaryButton>
    </div>
  </form>
</template>
