import { useQuery } from '@tanstack/vue-query'
import PaymentService from '@/services/payment'

const useCurrentPaymentRate = () => {
  return useQuery({
    queryKey: ['currentPaymentRate'],
    queryFn: async () => {
      return await PaymentService.getCurrentRate()
    },
  })
}

export default useCurrentPaymentRate
