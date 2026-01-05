import { PropertyService } from '@/services/property'
import { useQuery } from '@tanstack/vue-query'

const usePropertyDetails = (slug: string) => {
  return useQuery({
    queryKey: ['property', slug],
    queryFn: async () => {
      return await PropertyService.getPropertyDetails(slug)
    },
    staleTime: 1000 * 60 * 0.5, // Keep data fresh for 30 seconds
  })
}

export default usePropertyDetails
