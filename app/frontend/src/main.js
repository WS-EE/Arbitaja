import "./assets/css/main.css"

import 'bootstrap-icons/font/bootstrap-icons.css'
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap";

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from "./router";
import VueCookies from 'vue-cookies'
import axios from "axios";
import PrimeVue from 'primevue/config';
import Aura from '@primevue/themes/aura'
import { ensureAuthRehydrated } from '@/composables/useAuthRehydrate'
import { API_BASE_URL } from '@/services/http'

// Use enviromental variables for axios endpoint
console.log('Using backend api endpoint of: ' + API_BASE_URL)
axios.defaults.baseURL = API_BASE_URL
axios.defaults.withCredentials = true
axios.defaults.headers.post['Content-Type'] = 'application/json';
axios.defaults.headers.put['Content-Type'] = 'application/json';

const app = createApp(App);
const pinia = createPinia();
app.use(pinia);
app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      darkModeSelector: '.dark'
    }
  }
})
app.use(VueCookies, { expires: '7d'}, PrimeVue);

// Set arbitaja version
app.config.globalProperties.$arbitajaVersion = "devel-build"

// Set copyright Header to use
app.config.globalProperties.$copyrightHeader = "Copyright (c) 2025-2026 WorldSkills Estonia"

app.use(router);

// Populate userStore before mounting so all components see auth state immediately
ensureAuthRehydrated().finally(() => {
  app.mount('#app');
});
