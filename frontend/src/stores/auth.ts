import { ref } from 'vue'
import { defineStore } from 'pinia'
import type { components } from '@/api/generated/schema'
import client from '@/api/client'

type UserProfile = components['schemas']['UserResponse']

export const useAuthStore = defineStore('auth', () => {
  const isAuthenticated = ref(false)
  const accessToken = ref<string | null>(null)
  const refreshToken = ref<string | null>(null)
  const expirationDate = ref<number | null>(null)
  const userProfile = ref<UserProfile | null>(null)

  // Check values from the localStorage
  const loadFromLocalStorage = async () => {
    if (isAuthenticated.value) return
    const storedAccessToken = localStorage.getItem('accessToken')
    const storedRefreshToken = localStorage.getItem('refreshToken')
    const storedExpirationDate = localStorage.getItem('expirationDate')

    if (storedAccessToken && storedRefreshToken && storedExpirationDate) {
      accessToken.value = storedAccessToken
      refreshToken.value = storedRefreshToken
      expirationDate.value = parseInt(storedExpirationDate, 10)

      // todo: check expirationDate and refresh token if needed

      // Validate by getting user profile
      const { data, error } = await client.GET('/identity/user', {
        headers: {
          Authorization: `Bearer ${accessToken.value}`,
        },
      })

      if (isAuthenticated.value) return
      if (error)
        // logout if token is invalid
        return

      userProfile.value = data
      isAuthenticated.value = true
    }
  }
  loadFromLocalStorage()
})
