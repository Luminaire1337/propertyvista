import client, { normalizeError } from '@/api/client'
import type { components } from '../api/generated/schema'

export type PaymentIntent = Required<components['schemas']['PaymentIntentResponse']>
export type CurrentRate = Required<components['schemas']['PaymentRateResponse']>
export type CreatePaymentIntentRequest = components['schemas']['CreatePaymentIntentRequest']

export default abstract class PaymentService {
  static async getCurrentRate(): Promise<CurrentRate> {
    const { data, error } = await client.GET('/payments/rate')
    if (error) throw new Error(normalizeError(error))
    return data as CurrentRate
  }

  static async createPaymentIntent(
    paymentData: CreatePaymentIntentRequest,
  ): Promise<PaymentIntent> {
    const { data, error } = await client.POST('/payments/create-intent', {
      body: paymentData,
    })
    if (error) throw new Error(normalizeError(error))
    return data as PaymentIntent
  }
}
