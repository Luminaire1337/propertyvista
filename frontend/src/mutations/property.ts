import {
  PropertyService,
  type CreatePropertyRequest,
  type UpdatePropertyRequest,
} from '@/services/property'
import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { toast } from 'vue-sonner'
import { useRouter } from 'vue-router'

export type PartialUpdatePropertyMutationParameters = {
  slug: string
  updateData: UpdatePropertyRequest
}

export const useCreatePropertyMutation = () => {
  const queryClient = useQueryClient()
  const router = useRouter()
  return useMutation({
    mutationFn: async (propertyData: CreatePropertyRequest) => {
      return await PropertyService.createProperty(propertyData)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['userProperties'] })
      toast.success('Ogłoszenie zostało pomyślnie dodane!')
      router.push({ name: 'user-properties' })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const usePartiallyUpdatePropertyMutation = () => {
  const queryClient = useQueryClient()
  const router = useRouter()
  return useMutation({
    mutationFn: async (partialUpdateData: PartialUpdatePropertyMutationParameters) => {
      return await PropertyService.partiallyUpdateProperty(
        partialUpdateData.slug,
        partialUpdateData.updateData,
      )
    },
    onSuccess: (_, { slug }) => {
      queryClient.invalidateQueries({ queryKey: ['property', slug] })
      queryClient.invalidateQueries({ queryKey: ['userProperties'] })
      toast.success('Ogłoszenie zostało pomyślnie zaktualizowane!')
      router.push({ name: 'user-properties' })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const useDeletePropertyMutation = () => {
  const queryClient = useQueryClient()
  const router = useRouter()
  return useMutation({
    mutationFn: async (slug: string) => {
      return await PropertyService.deleteProperty(slug)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['userProperties'] })
      toast.success('Ogłoszenie zostało pomyślnie usunięte!')
      router.push({ name: 'user-properties' })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}
