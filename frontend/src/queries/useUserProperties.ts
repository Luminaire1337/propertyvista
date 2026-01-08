import { PropertyService, type PropertyPaginationRequest } from '@/services/property'
import { useQuery } from '@tanstack/vue-query'
import { computed, toValue, type MaybeRefOrGetter } from 'vue'

const useUserProperties = (paginationData: MaybeRefOrGetter<PropertyPaginationRequest>) => {
  const params = computed(() => toValue(paginationData))
  return useQuery({
    queryKey: computed(() => ['userProperties', params.value]),
    queryFn: async () => {
      return await PropertyService.getUserProperties(params.value)
    },
  })
}

export default useUserProperties
