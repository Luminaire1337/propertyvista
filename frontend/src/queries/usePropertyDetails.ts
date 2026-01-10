import PropertyService from '@/services/property'
import { useQuery } from '@tanstack/vue-query'
import { computed, toValue, type MaybeRefOrGetter } from 'vue'

const usePropertyDetails = (slug: MaybeRefOrGetter<string>) => {
  const slugValue = computed(() => toValue(slug))
  return useQuery({
    queryKey: computed(() => ['property', slugValue.value]),
    queryFn: async () => {
      return await PropertyService.getPropertyDetails(slugValue.value)
    },
  })
}

export default usePropertyDetails
