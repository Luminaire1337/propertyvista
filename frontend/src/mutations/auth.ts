import type { components } from '@/api/generated/schema'
import router from '@/router'
import AuthService, { type Auth } from '@/services/auth'
import { UserService } from '@/services/user'
import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { toast } from 'vue-sonner'

type AuthLocalStorageData = {
  accessToken: string
  refreshToken: string
  expirationDate: number
}

let authData: AuthLocalStorageData | null = null

// Local storage handling
const loadStorageData = () => {
  const accessToken = localStorage.getItem('accessToken')
  const refreshToken = localStorage.getItem('refreshToken')
  const expirationDateStr = localStorage.getItem('expirationDate')

  if (accessToken && refreshToken && expirationDateStr) {
    const expirationDate = parseInt(expirationDateStr, 10)
    authData = { accessToken, refreshToken, expirationDate }
  } else {
    // Incomplete data, clear authData
    authData = null
  }
}

const saveStorageData = (data: Auth) => {
  // Prepare AuthLocalStorageData object
  const newData: AuthLocalStorageData = {
    accessToken: data.accessToken,
    refreshToken: data.refreshToken,
    expirationDate: Date.now() / 1000 + data.expirationMs,
  }

  localStorage.setItem('accessToken', newData.accessToken)
  localStorage.setItem('refreshToken', newData.refreshToken)
  localStorage.setItem('expirationDate', newData.expirationDate.toString())
  authData = newData
}

const clearStorageData = () => {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('expirationDate')
  authData = null
}

// Load data on initialization
loadStorageData()

// Helper to check authentication status
export const isAuthenticated = () => !!authData

// Access token handling
const refreshAccessToken = async (): Promise<string | null> => {
  if (!authData) return null

  try {
    const response = await AuthService.refreshToken({
      token: authData.refreshToken,
    })
    saveStorageData(response)
    return response.accessToken
  } catch (error: Error | unknown) {
    console.error('Error refreshing access token:', error)
    // log the user out on token refresh failure
    useLogoutMutation().mutate()
    return null
  }
}

export const getAccessToken = async () => {
  if (!authData) return null

  const currentTime = Date.now() / 1000
  if (currentTime < authData.expirationDate) {
    return authData.accessToken
  }

  return await refreshAccessToken()
}

// Vue Query mutations
type LoginCredentials = components['schemas']['LoginRequest']
export const useLoginMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async (credentials: LoginCredentials) => {
      if (isAuthenticated()) throw new Error('Jesteś już zalogowany')
      const response = await AuthService.login(credentials)
      return response
    },
    onSuccess: async (data) => {
      saveStorageData(data)
      await queryClient.invalidateQueries({ queryKey: ['currentUser'] })

      toast.success('Pomyślnie zalogowano!')
      router.push({ name: 'home' })
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

type RegisterCredentials = components['schemas']['RegisterRequest']
export const useRegisterMutation = () => {
  return useMutation({
    mutationFn: async (credentials: RegisterCredentials) => {
      if (isAuthenticated()) throw new Error('Jesteś już zalogowany')
      const response = await UserService.register(credentials)
      return response
    },
    onSuccess: () => {
      toast.success('Pomyślnie zarejestrowano konto!')
    },
    onError: (error: Error) => {
      toast.error(error.message)
    },
  })
}

export const useLogoutMutation = () => {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: async () => {
      if (!isAuthenticated()) throw new Error('Nie jesteś zalogowany')
      await AuthService.logout({ token: authData!.refreshToken })
    },
    onSettled: () => {
      // Clear data regardless of success or failure
      clearStorageData()
      queryClient.invalidateQueries({ queryKey: ['currentUser'] })
      queryClient.setQueryData(['currentUser'], null)

      toast.success('Pomyślnie wylogowano!')
      router.push({ name: 'home' })
    },
  })
}
