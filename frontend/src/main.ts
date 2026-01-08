import { createApp } from 'vue'
import { VueQueryPlugin } from '@tanstack/vue-query'

import 'vue-sonner/style.css'
import './assets/css/main.css'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(VueQueryPlugin, {
  queryClientConfig: {
    defaultOptions: {
      queries: {
        // Run garbage collection every 5 minutes
        gcTime: 1000 * 60 * 5,
        // Stale data after 30 seconds
        staleTime: 1000 * 30,
      },
    },
  },
  // Only enable devtools in development
  enableDevtoolsV6Plugin: import.meta.env.DEV,
})
app.use(router)

app.mount('#app')
