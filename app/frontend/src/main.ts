import './assets/css/main.css'

import 'bootstrap-icons/font/bootstrap-icons.css'
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import VueCookies from 'vue-cookies'
import PrimeVue from 'primevue/config'
import Aura from '@primevue/themes/aura'
import { ensureAuthRehydrated } from '@/composables/useAuthRehydrate'

const app = createApp(App)
const pinia = createPinia()
app.use(pinia)
app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      darkModeSelector: '.dark',
    },
  },
})
app.use(VueCookies, { expires: '7d' })
app.use(router)

ensureAuthRehydrated().finally(() => {
  app.mount('#app')
})