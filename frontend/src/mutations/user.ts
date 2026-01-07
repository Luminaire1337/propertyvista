import {
  UserService,
  type TokenRequest,
  type UpdateUserAvatarRequest,
  type UpdateUserEmailRequest,
  type UpdateUserInfoRequest,
  type UpdateUserPasswordRequest,
} from '@/services/user'
import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { toast } from 'vue-sonner'
import { useRouter } from 'vue-router'
import { useLogoutMutation } from './auth'

export const useVerifyEmailMutation = () => {
  const router = useRouter()
  return useMutation({
    mutationFn: async (tokenData: TokenRequest) => {
      await UserService.verifyEmail(tokenData)
    },
    onSuccess: () => {
      toast.success('E-mail został pomyślnie zweryfikowany!')
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
    mutationFn: async () => {
      return await UserService.deleteUser()
    },
    onSuccess: () => {
      logoutMutation.mutate()
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
    mutationFn: async (emailData: UpdateUserEmailRequest) => {
      return await UserService.updateUserEmail(emailData)
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['currentUser'] })
      toast.success('E-mail został pomyślnie zaktualizowany!')
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const useUpdateUserPasswordMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (passwordData: UpdateUserPasswordRequest) => {
      return await UserService.updateUserPassword(passwordData)
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['currentUser'] })
      toast.success('Hasło zostało pomyślnie zmienione!')
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const useUpdateUserInfoMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (infoData: UpdateUserInfoRequest) => {
      return await UserService.updateUserInfo(infoData)
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['currentUser'] })
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
    mutationFn: async (avatarData: UpdateUserAvatarRequest) => {
      return await UserService.updateUserAvatar(avatarData)
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['currentUser'] })
      toast.success('Awatar został pomyślnie zaktualizowany!')
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const useDeleteUserAvatarMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async () => {
      return await UserService.deleteUserAvatar()
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['currentUser'] })
      toast.success('Awatar został pomyślnie usunięty!')
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const useInvalidateCurrentUserQuery = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async () => {
      await queryClient.invalidateQueries({ queryKey: ['currentUser'] })
    },
  })
}
