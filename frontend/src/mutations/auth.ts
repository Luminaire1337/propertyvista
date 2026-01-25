import AuthService, { type Auth, type LoginRequest } from '@/services/auth'
import UserService, { type RegisterRequest } from '@/services/user'
import { useMutation, useQueryClient } from '@tanstack/vue-query'
import { toast } from 'vue-sonner'
import { useRouter } from 'vue-router'

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
    expirationDate: Date.now() + data.expirationMs,
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
let isRefreshing = false
const refreshAccessToken = async (): Promise<string | null> => {
  if (!authData || isRefreshing) return null

  try {
    isRefreshing = true
    const response = await AuthService.refreshToken({
      token: authData.refreshToken,
    })
    saveStorageData(response)
    return response.accessToken
  } catch (error: Error | unknown) {
    console.error('Error refreshing access token:', error)
    clearStorageData()
    return null
  } finally {
    isRefreshing = false
  }
}

export const getAccessToken = async () => {
  if (!authData) return null

  if (Date.now() < authData.expirationDate) {
    return authData.accessToken
  }

  return await refreshAccessToken()
}

// Vue Query mutations
export const useLoginMutation = () => {
  const queryClient = useQueryClient()
  const router = useRouter()
  return useMutation({
    mutationFn: async (loginData: LoginRequest) => {
      if (isAuthenticated()) throw new Error('Jesteś już zalogowany')
      const response = await AuthService.login(loginData)
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

export const useRegisterMutation = () => {
  return useMutation({
    mutationFn: async (registerData: RegisterRequest) => {
      if (isAuthenticated()) throw new Error('Jesteś już zalogowany')
      const response = await UserService.register(registerData)
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
  const router = useRouter()
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

      // Clear user properties cache
      queryClient.invalidateQueries({ queryKey: ['userProperties'] })
      queryClient.setQueryData(['userProperties'], null)

      toast.success('Pomyślnie wylogowano!')
      router.push({ name: 'home' })
    },
  })
}
