import type { components } from '@/api/generated/schema'
import client, { normalizeError } from '@/api/client'

type User = {
  [K in keyof Omit<components['schemas']['UserResponse'], 'avatarImagePath'>]-?: Required<
    components['schemas']['UserResponse'][K]
  >
} & {
  avatarImagePath: string | null
}
type RegisterRequest = components['schemas']['RegisterRequest']
type TokenRequest = components['schemas']['TokenRequest']
type UpdateUserEmailRequest = components['schemas']['UpdateUserEmailRequest']
type UpdateUserPasswordRequest = components['schemas']['UpdateUserPasswordRequest']
type UpdateUserInfoRequest = components['schemas']['UpdateUserInfoRequest']

export abstract class UserService {
  static async register(userData: RegisterRequest): Promise<User> {
    const { data, error } = await client.POST('/users', { body: userData })
    if (error) throw new Error(normalizeError(error))
    return data as User
  }

  static async verifyEmail(tokenData: TokenRequest): Promise<void> {
    const { error } = await client.POST('/users/verify-email', { body: tokenData })
    if (error) throw new Error(normalizeError(error))
  }

  static async getUser(): Promise<User> {
    const { data, error } = await client.GET('/users/me')
    if (error) throw new Error(normalizeError(error))
    return data as User
  }

  static async deleteUser(): Promise<User> {
    const { data, error } = await client.DELETE('/users/me')
    if (error) throw new Error(normalizeError(error))
    return data as User
  }

  static async updateUserEmail(emailData: UpdateUserEmailRequest): Promise<User> {
    const { data, error } = await client.PUT('/users/me/email', { body: emailData })
    if (error) throw new Error(normalizeError(error))
    return data as User
  }

  static async updateUserPassword(passwordData: UpdateUserPasswordRequest): Promise<User> {
    const { data, error } = await client.PUT('/users/me/password', { body: passwordData })
    if (error) throw new Error(normalizeError(error))
    return data as User
  }

  static async updateUserInfo(infoData: UpdateUserInfoRequest): Promise<User> {
    const { data, error } = await client.PUT('/users/me/info', { body: infoData })
    if (error) throw new Error(normalizeError(error))
    return data as User
  }

  static async updateUserAvatar(avatarImage: File): Promise<User> {
    const formData = new FormData()
    formData.append('avatarImage', avatarImage)

    const { data, error } = await client.PUT('/users/me/avatar', {
      // @ts-expect-error - FormData is compatible but TypeScript doesn't recognize it
      body: formData,
    })

    if (error) throw new Error(normalizeError(error))
    return data as User
  }

  static async deleteUserAvatar(): Promise<User> {
    const { data, error } = await client.DELETE('/users/me/avatar')
    if (error) throw new Error(normalizeError(error))
    return data as User
  }
}
