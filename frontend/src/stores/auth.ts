import { ref, watch, computed } from 'vue'
import { defineStore } from 'pinia'
import type { components } from '@/api/generated/schema'
import client, { setApiAccessToken, makeErrorResponseHumanReadable } from '@/api/client'

type UserProfile = components['schemas']['UserResponse']
type LoginRequest = components['schemas']['LoginRequest']
type RegisterRequest = components['schemas']['RegisterRequest']
type ErrorResponse = components['schemas']['ErrorResponse']

export const useAuthStore = defineStore('auth', () => {
  // State
  const accessToken = ref<string | null>(localStorage.getItem('accessToken'))
  const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'))
  const expirationDate = ref<number | null>(+localStorage.getItem('expirationDate')! || null)
  const userProfile = ref<UserProfile | null>(null)

  // Access token observer to update API client
  watch(
    accessToken,
    (newToken) => {
      setApiAccessToken(newToken)
    },
    { immediate: true },
  )

  // Getters
  const isAuthDataSet = computed(() => {
    return (
      accessToken.value !== null && refreshToken.value !== null && expirationDate.value !== null
    )
  })
  const isAwaitingAuthentication = computed(() => isAuthDataSet.value && userProfile.value === null)
  const isAuthenticated = computed(() => isAuthDataSet.value && userProfile.value !== null)
  const isAdmin = computed(
    () => isAuthenticated.value && userProfile.value?.role?.toLowerCase() === 'admin',
  )

  // Private actions
  const validateAccessTokenDuration = async () => {
    try {
      if (!isAuthDataSet.value) return

      const now = Math.floor(Date.now() / 1000)
      if (expirationDate.value && now >= expirationDate.value) {
        console.log('Auth Store: Access token expired, attempting to refresh.')

        // Attempt to refresh tokens
        const { data, error } = await client.POST('/identity/auth/refresh', {
          body: {
            refreshToken: refreshToken.value!,
          },
        })

        if (error) {
          console.error('Auth Store: Failed to refresh tokens:', error)
          await logout()
          return
        }

        // Update tokens and expiration date
        accessToken.value = data.accessToken!
        refreshToken.value = data.refreshToken!
        expirationDate.value = Math.floor(Date.now() / 1000) + data.expirationMs! / 1000

        // Persist to localStorage
        localStorage.setItem('accessToken', accessToken.value)
        localStorage.setItem('refreshToken', refreshToken.value)
        localStorage.setItem('expirationDate', expirationDate.value.toString())
      }
    } catch (error) {
      console.error('Auth Store: Error validating access token duration:', error)
    }
  }

  // Actions
  const fetchUserProfile = async () => {
    try {
      if (!isAwaitingAuthentication.value) return

      // Ensure token is valid
      await validateAccessTokenDuration()

      // Fetch user profile
      const { data, error } = await client.GET('/identity/user')

      if (isAuthenticated.value) {
        console.warn('Auth Store: fetchUserProfile aborted, user is already authenticated.')
        return
      }

      if (error) {
        console.error('Auth Store: Failed to fetch user profile:', error)
        await logout()
        return
      }

      userProfile.value = data
    } catch (error) {
      console.error('Auth Store: Error fetching user profile:', error)
    }
  }

  const logout = async () => {
    try {
      // Reset localStorage
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('expirationDate')

      // Inform API about logout if refresh token exists
      if (refreshToken.value) {
        await client.POST('/identity/auth/logout', {
          body: {
            refreshToken: refreshToken.value,
          },
        })
      }

      // Reset state
      accessToken.value = null
      refreshToken.value = null
      expirationDate.value = null
      userProfile.value = null
    } catch (error) {
      console.error('Auth Store: Error during logout:', error)
    }
  }

  const login = async (credentials: LoginRequest) => {
    try {
      if (isAuthenticated.value) {
        console.warn('Auth Store: login aborted, user is already authenticated.')
        return
      }

      const { data, error } = await client.POST('/identity/auth/login', {
        body: credentials,
      })

      if (error) throw error

      // Update tokens and expiration date
      accessToken.value = data.accessToken!
      refreshToken.value = data.refreshToken!
      expirationDate.value = Math.floor(Date.now() / 1000) + data.expirationMs! / 1000

      // Persist to localStorage
      localStorage.setItem('accessToken', accessToken.value)
      localStorage.setItem('refreshToken', refreshToken.value)
      localStorage.setItem('expirationDate', expirationDate.value.toString())
    } catch (error) {
      console.error('Auth Store: Error during login:', error)
      throw new Error('Login failed: ' + makeErrorResponseHumanReadable(error as ErrorResponse))
    }
  }

  const register = async (details: RegisterRequest) => {
    try {
      if (isAuthenticated.value) {
        console.warn('Auth Store: register aborted, user is already authenticated.')
        return
      }

      const { error } = await client.POST('/identity/user', {
        body: details,
      })

      if (error) throw error
    } catch (error) {
      console.error('Auth Store: Error during registration:', error)
      throw new Error(
        'Registration failed: ' + makeErrorResponseHumanReadable(error as ErrorResponse),
      )
    }
  }

  return {
    userProfile,
    isAwaitingAuthentication,
    isAuthenticated,
    isAdmin,
    fetchUserProfile,
    logout,
    login,
    register,
  }
})
