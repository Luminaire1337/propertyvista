import type { components } from '@/api/generated/schema'
import router from '@/router'
import { UserService } from '@/services/user'
import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { toast } from 'vue-sonner'
import { useLogoutMutation } from './auth'

type TokenRequest = components['schemas']['TokenRequest']
type UpdateUserEmailRequest = components['schemas']['UpdateUserEmailRequest']
type UpdateUserPasswordRequest = components['schemas']['UpdateUserPasswordRequest']
type UpdateUserRoleRequest = components['schemas']['UpdateUserRoleRequest']
type UpdateUserStatusRequest = components['schemas']['UpdateUserStatusRequest']
type UpdateUserInfoRequest = components['schemas']['UpdateUserInfoRequest']

export const useVerifyEmailMutation = () => {
  return useMutation({
    mutationFn: async (tokenData: TokenRequest) => {
      await UserService.verifyEmail(tokenData)
    },
    onSuccess: () => {
      toast.success('Email został pomyślnie zweryfikowany!')
      router.push({ name: 'login' })
    },
    onError: (error: Error) => {
      toast.error(error.message)
      router.push({ name: 'home' })
    },
  })
}

export const useDeleteUserMutation = () => {
  const logoutMutation = useLogoutMutation()
  return useMutation({
    mutationFn: async (id: string | 'me' = 'me') => {
      return await UserService.deleteUser(id)
    },
    onSuccess: (_, id) => {
      if (id === 'me') logoutMutation.mutate()

      toast.success('Konto zostało pomyślnie usunięte!')
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const useUpdateUserEmailMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({
      id = 'me',
      emailData,
    }: {
      id?: string | 'me'
      emailData: UpdateUserEmailRequest
    }) => {
      return await UserService.updateUserEmail(id, emailData)
    },
    onSuccess: (_, data) => {
      if (data.id === 'me') queryClient.invalidateQueries({ queryKey: ['currentUser'] })
      toast.success('Email został pomyślnie zaktualizowany!')
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const useUpdateUserPasswordMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({
      id = 'me',
      passwordData,
    }: {
      id?: string | 'me'
      passwordData: UpdateUserPasswordRequest
    }) => {
      return await UserService.updateUserPassword(id, passwordData)
    },
    onSuccess: (_, data) => {
      if (data.id === 'me') queryClient.invalidateQueries({ queryKey: ['currentUser'] })
      toast.success('Hasło zostało pomyślnie zmienione!')
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const useUpdateUserRoleMutation = () => {
  return useMutation({
    mutationFn: async ({ id, roleData }: { id: string; roleData: UpdateUserRoleRequest }) => {
      return await UserService.updateUserRole(id, roleData)
    },
    onSuccess: () => {
      toast.success('Rola użytkownika została pomyślnie zaktualizowana!')
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const useUpdateUserStatusMutation = () => {
  return useMutation({
    mutationFn: async ({ id, statusData }: { id: string; statusData: UpdateUserStatusRequest }) => {
      return await UserService.updateUserStatus(id, statusData)
    },
    onSuccess: () => {
      toast.success('Status użytkownika został pomyślnie zaktualizowany!')
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const useUpdateUserInfoMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({
      id = 'me',
      infoData,
    }: {
      id?: string | 'me'
      infoData: UpdateUserInfoRequest
    }) => {
      return await UserService.updateUserInfo(id, infoData)
    },
    onSuccess: (_, data) => {
      if (data.id === 'me') queryClient.invalidateQueries({ queryKey: ['currentUser'] })
      toast.success('Informacje zostały pomyślnie zaktualizowane!')
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const useUpdateUserAvatarMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async ({ id = 'me', avatarImage }: { id?: string | 'me'; avatarImage: File }) => {
      return await UserService.updateUserAvatar(id, avatarImage)
    },
    onSuccess: (_, data) => {
      if (data.id === 'me') queryClient.invalidateQueries({ queryKey: ['currentUser'] })
      toast.success('Avatar został pomyślnie zaktualizowany!')
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const useDeleteUserAvatarMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (id: string | 'me' = 'me') => {
      return await UserService.deleteUserAvatar(id)
    },
    onSuccess: (_, id) => {
      if (id === 'me') queryClient.invalidateQueries({ queryKey: ['currentUser'] })
      toast.success('Avatar został pomyślnie usunięty!')
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}
