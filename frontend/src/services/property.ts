import type { components } from '@/api/generated/schema'
import client, { normalizeError } from '@/api/client'
import type { RecursiveRequired } from '@/utils'

export type Property = Required<components['schemas']['PropertyResponse']>
export type PropertyPage = RecursiveRequired<components['schemas']['PropertyPageResponse']>
export type PropertyDetails = RecursiveRequired<components['schemas']['PropertyDetailedResponse']>
export type PropertyPaginationRequest = components['schemas']['PropertyPaginationRequest']
export type CreatePropertyRequest = Omit<
  components['schemas']['CreatePropertyRequest'],
  'images'
> & {
  images: File[]
}
export type SearchFilters = Omit<
  PropertyPaginationRequest,
  'page' | 'size' | 'sortField' | 'sortDirection'
>

export abstract class PropertyService {
  static async getProperties(paginationData: PropertyPaginationRequest): Promise<PropertyPage> {
    const { data, error } = await client.GET('/properties', {
      params: {
        // @ts-expect-error - TypeScript cannot infer the correct type here
        query: { ...paginationData },
      },
    })
    if (error) throw new Error(normalizeError(error))
    return data as PropertyPage
  }

  static async getUserProperties(paginationData: PropertyPaginationRequest): Promise<PropertyPage> {
    const { data, error } = await client.GET('/properties/me', {
      params: {
        // @ts-expect-error - TypeScript cannot infer the correct type here
        query: { ...paginationData },
      },
    })
    if (error) throw new Error(normalizeError(error))
    return data as PropertyPage
  }

  static async createProperty(propertyData: CreatePropertyRequest): Promise<Property> {
    const formData = new FormData()
    for (const [key, value] of Object.entries(propertyData)) {
      if (key === 'images' && Array.isArray(value)) {
        value.forEach((file) => formData.append('images', file))
      } else {
        formData.append(key, String(value))
      }
    }

    const { data, error } = await client.POST('/properties', {
      // @ts-expect-error - FormData is compatible but TypeScript doesn't recognize it
      body: formData,
    })
    if (error) throw new Error(normalizeError(error))
    return data as Property
  }

  static async getPropertyDetails(slug: string): Promise<PropertyDetails> {
    const { data, error } = await client.GET('/properties/{slug}', {
      params: {
        path: { slug },
      },
    })
    if (error) throw new Error(normalizeError(error))
    return data as PropertyDetails
  }
}
