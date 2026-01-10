import UserService from '@/services/user'
import { useQuery } from '@tanstack/vue-query'
import { isAuthenticated, useLogoutMutation } from '@/mutations/auth'
import { watch, onScopeDispose } from 'vue'

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
  const stopWatcher = watch(
    () => query.isError.value,
    (isError) => {
      if (isError && isAuthenticated()) {
        logoutMutation.mutate()
      }
    },
  )

  // Clean up watcher when component is destroyed to prevent memory leaks
  onScopeDispose(() => {
    stopWatcher()
  })

  return query
}

export default useCurrentUser
