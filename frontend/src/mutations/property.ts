import { PropertyService, type CreatePropertyRequest } from '@/services/property'
import { useMutation } from '@tanstack/vue-query'
import { toast } from 'vue-sonner'

export const useCreatePropertyMutation = () => {
  return useMutation({
    mutationFn: async (propertyData: CreatePropertyRequest) => {
      return await PropertyService.createProperty(propertyData)
    },
    onSuccess: () => {
      // queryClient.invalidateQueries({ queryKey: ['my-properties'] })
      toast.success('Nieruchomość została pomyślnie dodana!')
      // router.push({ name: 'my-properties' })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}
