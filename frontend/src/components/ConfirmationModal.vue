<script setup lang="ts">
import {
  Dialog,
  DialogPanel,
  DialogTitle,
  DialogDescription,
  TransitionRoot,
  TransitionChild,
} from '@headlessui/vue'

defineProps<{
  isOpen: boolean
  title: string
  description: string
  confirmText?: string
  cancelText?: string
  isPending?: boolean
  variant?: 'danger' | 'primary'
}>()

const emit = defineEmits<{
  close: []
  confirm: []
}>()
</script>

<template>
  <TransitionRoot :show="isOpen" as="template">
    <Dialog @close="emit('close')" class="relative z-50">
      <TransitionChild
        as="template"
        enter="ease-out duration-300"
        enter-from="opacity-0"
        enter-to="opacity-100"
        leave="ease-in duration-200"
        leave-from="opacity-100"
        leave-to="opacity-0"
      >
        <div class="fixed inset-0 bg-black/30" aria-hidden="true" />
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
          <DialogPanel class="w-full max-w-md bg-white rounded-lg shadow-lg p-6">
            <DialogTitle
              class="text-xl font-semibold mb-3"
              :class="variant === 'danger' ? 'text-red-600' : 'text-gray-900'"
            >
              {{ title }}
            </DialogTitle>
            <DialogDescription class="text-sm text-gray-600 mb-4">
              {{ description }}
            </DialogDescription>

            <slot name="content"></slot>

            <div class="flex space-x-3" :class="$slots.content ? 'mt-4' : ''">
              <button
                @click="emit('confirm')"
                type="button"
                :disabled="isPending"
                class="flex-1 px-4 py-2 text-white rounded transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed"
                :class="
                  variant === 'danger'
                    ? 'bg-red-600 hover:bg-red-700'
                    : 'bg-primary hover:bg-primary-dark'
                "
              >
                {{ confirmText || 'Potwierdź' }}
              </button>
              <button
                @click="emit('close')"
                type="button"
                class="flex-1 px-4 py-2 border border-gray-300 rounded hover:bg-gray-50 transition-colors"
              >
                {{ cancelText || 'Anuluj' }}
              </button>
            </div>
          </DialogPanel>
        </TransitionChild>
      </div>
    </Dialog>
  </TransitionRoot>
</template>
