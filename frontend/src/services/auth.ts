import client, { normalizeError } from '@/api/client'
import type { components } from '../api/generated/schema'

export type Auth = Required<components['schemas']['AuthResponse']>
type LoginRequest = components['schemas']['LoginRequest']
type TokenRequest = components['schemas']['TokenRequest']

export default abstract class AuthService {
  static async login(loginData: LoginRequest): Promise<Auth> {
    const { data, error } = await client.POST('/auth/login', { body: loginData })
    if (error) throw new Error(normalizeError(error))
    return data as Auth
  }

  static async logout(tokenData: TokenRequest): Promise<void> {
    const { error } = await client.POST('/auth/logout', { body: tokenData })
    if (error) throw new Error(normalizeError(error))
  }

  static async refreshToken(tokenData: TokenRequest): Promise<Auth> {
    const { data, error } = await client.POST('/auth/refresh', { body: tokenData })
    if (error) throw new Error(normalizeError(error))
    return data as Auth
  }
}
