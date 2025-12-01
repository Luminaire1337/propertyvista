import { createApp } from 'vue'
import { createPinia } from 'pinia'

import 'vue-sonner/style.css'
import './assets/css/main.css'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.use(router)

await router.isReady()
app.mount('#app')
