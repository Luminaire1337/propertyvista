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
type UpdateUserRoleRequest = components['schemas']['UpdateUserRoleRequest']
type UpdateUserStatusRequest = components['schemas']['UpdateUserStatusRequest']
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

  static async getUser(id: string | 'me'): Promise<User> {
    const response =
      id === 'me'
        ? await client.GET('/users/me')
        : await client.GET('/users/{id}', { params: { path: { id } } })

    if (response.error) throw new Error(normalizeError(response.error))
    return response.data as User
  }

  static async deleteUser(id: string | 'me'): Promise<User> {
    const response =
      id === 'me'
        ? await client.DELETE('/users/me')
        : await client.DELETE('/users/{id}', { params: { path: { id } } })

    if (response.error) throw new Error(normalizeError(response.error))
    return response.data as User
  }

  static async updateUserEmail(
    id: string | 'me',
    emailData: UpdateUserEmailRequest,
  ): Promise<User> {
    const response =
      id === 'me'
        ? await client.PUT('/users/me/email', { body: emailData })
        : await client.PUT('/users/{id}/email', { body: emailData, params: { path: { id } } })

    if (response.error) throw new Error(normalizeError(response.error))
    return response.data as User
  }

  static async updateUserPassword(
    id: string | 'me',
    passwordData: UpdateUserPasswordRequest,
  ): Promise<User> {
    const response =
      id === 'me'
        ? await client.PUT('/users/me/password', { body: passwordData })
        : await client.PUT('/users/{id}/password', { body: passwordData, params: { path: { id } } })

    if (response.error) throw new Error(normalizeError(response.error))
    return response.data as User
  }

  static async updateUserRole(id: string, roleData: UpdateUserRoleRequest): Promise<User> {
    const { data, error } = await client.PUT('/users/{id}/role', {
      body: roleData,
      params: { path: { id } },
    })
    if (error) throw new Error(normalizeError(error))
    return data as User
  }

  static async updateUserStatus(id: string, statusData: UpdateUserStatusRequest): Promise<User> {
    const { data, error } = await client.PUT('/users/{id}/status', {
      body: statusData,
      params: { path: { id } },
    })
    if (error) throw new Error(normalizeError(error))
    return data as User
  }

  static async updateUserInfo(id: string | 'me', infoData: UpdateUserInfoRequest): Promise<User> {
    const response =
      id === 'me'
        ? await client.PUT('/users/me/info', { body: infoData })
        : await client.PUT('/users/{id}/info', { body: infoData, params: { path: { id } } })

    if (response.error) throw new Error(normalizeError(response.error))
    return response.data as User
  }

  static async updateUserAvatar(id: string | 'me', avatarImage: File): Promise<User> {
    // https://github.com/openapi-ts/openapi-typescript/issues/1214#issuecomment-2662177105
    const response =
      id === 'me'
        ? await client.PUT('/users/me/avatar', { file: avatarImage as unknown as string })
        : await client.PUT('/users/{id}/avatar', {
            file: avatarImage as unknown as string,
            params: { path: { id } },
          })

    if (response.error) throw new Error(normalizeError(response.error))
    return response.data as User
  }

  static async deleteUserAvatar(id: string | 'me'): Promise<User> {
    const response =
      id === 'me'
        ? await client.DELETE('/users/me/avatar')
        : await client.DELETE('/users/{id}/avatar', { params: { path: { id } } })

    if (response.error) throw new Error(normalizeError(response.error))
    return response.data as User
  }
}
