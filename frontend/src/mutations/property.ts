import router from '@/router'
import {
  PropertyService,
  type CreatePropertyRequest,
  type UpdatePropertyRequest,
} from '@/services/property'
import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { toast } from 'vue-sonner'

export const useCreatePropertyMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (propertyData: CreatePropertyRequest) => {
      return await PropertyService.createProperty(propertyData)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['userProperties'] })
      toast.success('Nieruchomość została pomyślnie dodana!')
      router.push({ name: 'user-properties' })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const usePartiallyUpdatePropertyMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({
      slug,
      updateData,
    }: {
      slug: string
      updateData: UpdatePropertyRequest
    }) => {
      return await PropertyService.partiallyUpdateProperty(slug, updateData)
    },
    onSuccess: (_, { slug }) => {
      queryClient.invalidateQueries({ queryKey: ['property', slug] })
      queryClient.invalidateQueries({ queryKey: ['userProperties'] })
      toast.success('Nieruchomość została pomyślnie zaktualizowana!')
      router.push({ name: 'user-properties' })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}
