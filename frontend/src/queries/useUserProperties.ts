import { PropertyService, type PropertyPaginationRequest } from '@/services/property'
import { useQuery } from '@tanstack/vue-query'

const useUserProperties = (paginationData: PropertyPaginationRequest) => {
  return useQuery({
    queryKey: ['userProperties', paginationData],
    queryFn: async () => {
      return await PropertyService.getUserProperties(paginationData)
    },
    staleTime: 1000 * 60 * 0.5, // Keep data fresh for 30 seconds
  })
}

export default useUserProperties
