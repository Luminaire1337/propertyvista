import router from '@/router'
import { PropertyService, type CreatePropertyRequest } from '@/services/property'
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
