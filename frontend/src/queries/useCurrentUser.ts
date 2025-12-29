import { UserService } from '@/services/user'
import { useQuery } from '@tanstack/vue-query'
import { isAuthenticated, useLogoutMutation } from '@/mutations/auth'
import { watch } from 'vue'

const useCurrentUser = () => {
  const logoutMutation = useLogoutMutation()

  const query = useQuery({
    queryKey: ['currentUser'],
    queryFn: async () => {
      return (await UserService.getUser()) || null
    },
    retry: false,
    staleTime: 1000 * 60 * 5, // Keep data fresh for 5 minutes
  })

  // Logout user when fetch fails (e.g., 401 Unauthorized)
  watch(
    () => query.isError.value,
    (isError) => {
      if (isError && isAuthenticated()) {
        logoutMutation.mutate()
      }
    },
  )

  return query
}

export default useCurrentUser
