import { PropertyService, type PropertyPaginationRequest } from '@/services/property'
import { useQuery } from '@tanstack/vue-query'
import { computed, toValue, type MaybeRefOrGetter } from 'vue'

const usePropertyPage = (paginationData: MaybeRefOrGetter<PropertyPaginationRequest>) => {
  const params = computed(() => toValue(paginationData))
  return useQuery({
    queryKey: computed(() => ['properties', params.value]),
    queryFn: async () => {
      return await PropertyService.getProperties(params.value)
    },
  })
}

export default usePropertyPage
