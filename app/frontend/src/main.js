import "./assets/css/main.css"

import 'bootstrap-icons/font/bootstrap-icons.css'
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap";

import { createApp } from 'vue'
import App from './App.vue'
import router from "./router";
import VueCookies from 'vue-cookies'
import axios from "axios";
import PrimeVue from 'primevue/config';

// Use enviromental variables for axios endpoint
console.log('Using backend api endpoint of: ' + import.meta.env.VITE_APP_BASE_URL + import.meta.env.VITE_APP_API_ENDPOINT)
axios.defaults.baseURL = import.meta.env.VITE_APP_BASE_URL + import.meta.env.VITE_APP_API_ENDPOINT
axios.defaults.headers.post['Content-Type'] = 'application/json';
axios.defaults.headers.put['Content-Type'] = 'application/json';

const app = createApp(App);
app.use(VueCookies, { expires: '7d'}, PrimeVue);
app.use(router);
app.mount('#app');
