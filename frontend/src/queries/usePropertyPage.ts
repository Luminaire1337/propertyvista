import { PropertyService, type PropertyPaginationRequest } from '@/services/property'
import { useQuery } from '@tanstack/vue-query'

const usePropertyPage = (paginationData: PropertyPaginationRequest) => {
  return useQuery({
    queryKey: ['properties', paginationData],
    queryFn: async () => {
      return await PropertyService.getProperties(paginationData)
    },
    staleTime: 1000 * 60 * 0.5, // Keep data fresh for 30 seconds
  })
}

export default usePropertyPage
