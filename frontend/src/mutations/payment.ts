import type { CreatePaymentIntentRequest } from '@/services/payment'
import PaymentService from '@/services/payment'
import { useMutation } from '@tanstack/vue-query'
import { toast } from 'vue-sonner'

export const useCreatePaymentIntentMutation = () => {
  return useMutation({
    mutationFn: async (intentData: CreatePaymentIntentRequest) => {
      return await PaymentService.createPaymentIntent(intentData)
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}
